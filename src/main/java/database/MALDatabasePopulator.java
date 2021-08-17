package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.jsoup.HttpStatusException;

import scrapingmal.*;
import scrapingmal.helpers.Season;

/*
 * NEED TO REFACTOR
 * - take more time to connect in between seasons
 * - abstract some things into other helper classes
 */

public class MALDatabasePopulator {
	// look into adding ON CONFLICT (if applicable to INSERT statements)
	private static final String INSERT_SHOW = 
		"INSERT OR REPLACE INTO shows (romanized_title, japanese_title, english_title, premiere_term, premiere_year, type, link) "
		+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
	
	private static final String INSERT_CHARACTER = 
		"INSERT OR REPLACE INTO characters (name, link) VALUES(?, ?);";
	
	private static final String INSERT_PERSON = 
		"INSERT OR REPLACE INTO people (first_name, last_name, given_name, family_name, birthday, link) "
		+ "VALUES(?, ?, ?, ?, ?, ?);";
	
	private static final String INSERT_VOICE_ACTOR = 
		"INSERT OR REPLACE INTO voice_actors (va_id, show_id) "
		+ "VALUES(?, ?);";
	
	private static final String INSERT_ROLE = 
		"INSERT OR REPLACE INTO roles (va_id, char_id, show_id) "
		+ "VALUES(?, ?, ?);";
	
	private static final String INSERT_SHOW_CHAR = 
		"INSERT OR REPLACE INTO shows_chars (show_id, char_id) "
		+ "VALUES(?, ?);";
	
	private static final String INSERT_STAFF = 
		"INSERT OR REPLACE INTO staff (staff_id, show_id, position) "
		+ "VALUES(?, ?, ?);";
	
	private static final String FIND_CHAR_ID = 
		"SELECT id FROM characters WHERE link = ?;";
	
	private static final String FIND_PERSON_ID = 
		"SELECT id FROM people WHERE link = ?;";
	
	private static final String FILE = "currentseasonurl.txt";
	
	private static final int DEFAULT_ERROR_WAIT = 40000;
	
	private static int waitMillis = 1250;
	private static int seasonWait = 40000;
	private static int showWait = 20000;
	private static int personWait = 10000;
	private static int errorWait = DEFAULT_ERROR_WAIT;
	
	private Connection conn;
	private ArrayList<String> seasonURLs;
	private int startIndex;
	
	public MALDatabasePopulator(Connection conn) throws InterruptedException {
		this.conn = conn;
		this.seasonURLs = getSeasonURLs();
		this.startIndex = getStartIndex();
	}
	private ArrayList<String> getSeasonURLs() throws InterruptedException {
		ArchivePage archive = null;
		boolean connected = false;
		while (!connected) {
			try {
				archive = new ArchivePage();
				resetErrorWait();
				Thread.sleep(waitMillis);
				connected = true;
			} catch (HttpStatusException e) {
				errorWait();
			} catch (Exception e) {
				System.out.println("Error occurred");
				e.printStackTrace();
			}
		}
		return archive.getSeasonURLs();
	}
	private int getStartIndex() {
		int index = 0;
		File file = new File(FILE);
		if (file.exists()) {
			try (Scanner fileScan = new Scanner(file)) {
				index = seasonURLs.indexOf(fileScan.nextLine());
			} catch (FileNotFoundException e) {
				System.out.println("File not found exception thrown");
				e.printStackTrace();
			}
		}
		return index <= 0 ? 0 : index;
	}
	
	private void errorWait() throws InterruptedException {
		System.out.println("Attempting again in " + errorWait + " ms");
		Thread.sleep(errorWait);
		errorWait += 10000;
	}
	private void resetErrorWait() {
		if (errorWait != DEFAULT_ERROR_WAIT) {
			errorWait = DEFAULT_ERROR_WAIT;
		}
	}
	
	/*
	 * Need to:
	 * - iterate through seasonURLs and INSERT into the database for each season's shows
	 * 		- wait 40 secs between each season
	 * 		- wait 20 secs between each show
	 * 		- wait 10 secs between each person
	 * - upon an HttpStatusException, do errorWait
	 * - upon an UnknownHostException, drop all in the db related to the current season
	 * 		- for each show, I need to drop all roles, shows_chars, staff, and voice_actors with the show_id and
	 * 			then drop the show from shows
	 * 		- then, store the URL of the current season in an external file
	 * - upon successfully INSERTing all seasons, return true. Otherwise, false
	 */
	public boolean populate() throws Exception {
		int size = seasonURLs.size();
		for (int i = startIndex; i < size; i++) {
			try {
				System.out.println(seasonURLs.get(i));
				SeasonPage season = new SeasonPage(seasonURLs.get(i));
				resetErrorWait();
				Thread.sleep(seasonWait);
				
				insertSeason(season);
				
			} catch (HttpStatusException e) {
				errorWait();
				i--;
			} catch (UnknownHostException e) {
				System.out.println("Unknown host exception occurred");
				e.printStackTrace();
				recordCurrentSeasonURL(seasonURLs.get(i));
				throw e;
			} catch (Exception e) {
				System.out.println("Error occurred");
				e.printStackTrace();
				recordCurrentSeasonURL(seasonURLs.get(i));
				throw e;
			}
		}
		recordCurrentSeasonURL("COMPLETED");
		return true;
	}
	private void recordCurrentSeasonURL(String seasonURL) {
		try (PrintWriter fileWrite = new PrintWriter(new File(FILE))) {
			fileWrite.write(seasonURL);
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception thrown");
			e.printStackTrace();
		}
	}
	public void insertSeason(Season season) {
		try {
			SeasonPage seasonPage = new SeasonPage(seasonURLs.get(seasonURLs.indexOf("https://myanimelist.net/anime/season/" 
								+ season.getYear() + "/"
								+ season.getTerm().toString().toLowerCase())));
			insertSeason(seasonPage);
			Thread.sleep(seasonWait);
		} catch (HttpStatusException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void insertSeason(SeasonPage season) throws Exception {
		for (int i = 0; i < season.getURLs().size(); i++) {
			try {
				AnimePage show = new AnimePage(season.getURLs().get(i));
				resetErrorWait();
				
				show.setPremiereSeason(season.getSeason());
				Thread.sleep(showWait);
				
				insertAnime(show);
				
			} catch (HttpStatusException e) {
				errorWait();
				i--;
			} catch (Exception e) {
				throw e;
			}
		}
	}
	public void insertAnime(AnimePage show) throws Exception {
		try {
			int showId = insertShow(show);
			
			AnimeStaffPage staffPage = getStaffPage(show.getStaffURL());
			
			insertRoles(staffPage.getRolesURLs(), showId);
			insertStaff(staffPage.getStaffURLs(), showId);
		} catch (Exception e) {
			throw e;
		}
	}
	private int insertShow(AnimePage show) {
		try (PreparedStatement insertShow = conn.prepareStatement(INSERT_SHOW, Statement.RETURN_GENERATED_KEYS);) {
			insertShow.setString(1, show.getRomanizedTitle());
			insertShow.setString(2, show.getJapaneseTitle());
			insertShow.setString(3, show.getEnglishTitle());
			insertShow.setString(4, show.getPremiereSeason().getTerm().toString());
			insertShow.setInt(5, show.getPremiereSeason().getYear());
			insertShow.setString(6, show.getType().toString());
			insertShow.setString(7, show.getURL());
			insertShow.executeUpdate();
			return insertShow.getGeneratedKeys().getInt(1);
			//System.out.println("Inserted " + show.getRomanizedTitle() + " with id " + id);
			
		} catch (SQLException e) {
			System.out.println("Insertion into shows failed");
			System.out.println(e.getMessage());
			return -1;
		}
	}
	private AnimeStaffPage getStaffPage(String staffPageURL) throws Exception {
		AnimeStaffPage staff = null;
		while (staff == null) {
			try {
				System.out.println(staffPageURL);
				staff = new AnimeStaffPage(staffPageURL);
				Thread.sleep(showWait);
			} catch (HttpStatusException e) {
				errorWait();
			}
		}
		return staff;
	}
	private CharacterPage getCharacterPage(String charPageURL) throws Exception {
		CharacterPage character = null;
		while (character == null) {
			try {
				System.out.println(charPageURL);
				character = new CharacterPage(charPageURL);
				Thread.sleep(showWait);
			} catch (HttpStatusException e) {
				errorWait();
			}
		}
		return character;
	}
	private PersonPage getPersonPage(String personPageURL) throws Exception {
		PersonPage person = null;
		while (person == null) {
			try {
				System.out.println(personPageURL);
				person = new PersonPage(personPageURL);
				Thread.sleep(showWait);
			} catch (HttpStatusException e) {
				errorWait();
			}
		}
		return person;
	}
	/*
	 * IMPLEMENT HERE
	 * - get people and characters for show
	 * - may be able to fill in people, voice_actors, characters, and roles at once as I go through the roles HashMao?
	 * - fill in people and staff tables as I go through positions HashMap 
	 */
	private void insertRoles(HashMap<String, ArrayList<String>> roles, int showId) throws Exception {
		for (String charURL: roles.keySet()) {
			CharacterPage charPage = getCharacterPage(charURL);
			int charId = insertCharacter(charPage);
			insertShowChar(showId, charId);
			for (String vaURL: roles.get(charURL)) {
				PersonPage vaPage = getPersonPage(vaURL);
				int vaId = insertPerson(vaPage);
				insertVoiceActor(vaId, showId);
				insertRole(vaId, charId, showId);
			}
		}
	}
	private void insertStaff(HashMap<String, String> staff, int showId) throws Exception {
		for (String staffURL: staff.keySet()) {
			PersonPage staffPage = getPersonPage(staffURL);
			int staffId = insertPerson(staffPage);
			insertStaff(staffId, showId, staff.get(staffURL));
		}
	}
	// Inserting into characters table, returns id
	// Need to create CharacterPage
	private  int insertCharacter(CharacterPage charPage) throws Exception {
		int id = findId(charPage, FIND_CHAR_ID);
		if (id != -1) {
			return id;
		}
		
		// IMPLEMENT HERE; INSERT INTO characters TABLE
		try (PreparedStatement insertChar = conn.prepareStatement(INSERT_CHARACTER, Statement.RETURN_GENERATED_KEYS);) {
			insertChar.setString(1, charPage.getName());
			insertChar.setString(2, charPage.getURL());
			insertChar.executeUpdate();
			
			// return the inserted character row's id
			return insertChar.getGeneratedKeys().getInt(1);
		} catch (SQLException e) {
			System.out.println("Insertion into characters failed");
			System.out.println(e.getMessage());
		}
		
		return -1;
	}
	// Inserting into people table, returns id
	// Returns the id of the person if the person already is in the table
	// Need to create PersonPage
	private  int insertPerson(PersonPage personPage) throws Exception {
		int id = findId(personPage, FIND_PERSON_ID);
		if (id != -1) {
			return id;
		}
		
		try (PreparedStatement insertPerson = conn.prepareStatement(INSERT_PERSON, Statement.RETURN_GENERATED_KEYS);){
			insertPerson.setString(1, personPage.getPerson().getFirstName());
			insertPerson.setString(2, personPage.getPerson().getLastName());
			insertPerson.setString(3, personPage.getPerson().getGivenName());
			insertPerson.setString(4, personPage.getPerson().getFamilyName());
			insertPerson.setString(5, personPage.getPerson().getBirthday().toString());
			insertPerson.setString(6, personPage.getURL());
			insertPerson.executeUpdate();
			
			// return the inserted character row's id
			return insertPerson.getGeneratedKeys().getInt(1);
		} catch (SQLException e) {
			System.out.println("Insertion into people failed");
			System.out.println(e.getMessage());
		}
		return -1;
		
	}
	private int findId(Page page, String sqlQuery) {
		try (PreparedStatement selectPersonId = conn.prepareStatement(sqlQuery)) {
			selectPersonId.setString(1, page.getURL());
			ResultSet id = selectPersonId.executeQuery();
			if (id.next()) {
				System.out.println("ID " + id.getInt(1) + ": " + page.getURL());
				return id.getInt(1);
			}
			return -1;
		} catch (SQLException e) {
			System.out.println("Failed to query id from people table");
			System.out.println(e.getMessage());
			return -1;
		}
	}
	// Inserting into voice_actors table
	private  void insertVoiceActor(int vaId, int showId) {
		try (PreparedStatement insertVoiceActor = conn.prepareStatement(INSERT_VOICE_ACTOR);) {
			insertVoiceActor.setInt(1, vaId);
			insertVoiceActor.setInt(2, showId);
			insertVoiceActor.executeUpdate();

		} catch (SQLException e) {
			System.out.println("Insertion into voice_actors failed");
			System.out.println(e.getMessage());
		}
	}
	// Inserting into roles table
	private  void insertRole(int vaId, int charId, int showId) {
		try (PreparedStatement insertRole = conn.prepareStatement(INSERT_ROLE);) {
			insertRole.setInt(1, vaId);
			insertRole.setInt(2, charId);
			insertRole.setInt(3, showId);
			insertRole.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("Insertion into roles failed");
			System.out.println(e.getMessage());
		}
	}
	// Inserting into shows_chars table
	private  void insertShowChar(int showId, int charId) {
		try (PreparedStatement insertShowChar = conn.prepareStatement(INSERT_SHOW_CHAR);) {
			insertShowChar.setInt(1, showId);
			insertShowChar.setInt(2, charId);
			insertShowChar.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("Insertion into shows_chars failed");
			System.out.println(e.getMessage());
		}
	}
	// Inserting into and staff table
	private  void insertStaff(int staffId, int showId, String position) {
		try (PreparedStatement insertStaff = conn.prepareStatement(INSERT_STAFF);) {
			insertStaff.setInt(1, staffId);
			insertStaff.setInt(2, showId);
			insertStaff.setString(3, position);
			insertStaff.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("Insertion into voice_actors failed");
			System.out.println(e.getMessage());
		}
	}
}
