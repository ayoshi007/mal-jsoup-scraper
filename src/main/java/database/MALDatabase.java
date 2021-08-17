package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

import org.jsoup.HttpStatusException;

import scrapingmal.SeasonPage;
import scrapingmal.helpers.Season;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public class MALDatabase {
	private static final String FILE = "currentseasonurl.txt";
	
	private Connection conn;
	private MALDatabasePopulator populator;
	private MALDatabaseDropper dropper;
	
	private final String[] TABLE_NAMES = {
		"people", "shows", "characters", "voice_actors", "staff", "roles", "shows_chars"
	};
	
	public MALDatabase() throws InterruptedException {
		this("mal.db");
	}
	/*
	 * NEED TO IMPLEMENT:
	 * 	- inserting data using MAL scraping classes
	 * 		- need to create helper classes to:
	 * 			- create connections between scrapers
	 * 			- extract data from scrapers and INSERT into the database
	 * 	- a way to deal with HttpStatusExceptions with code 403
	 * 		- prob. create a try-catch block in a loop to wait an increasing amount of time if an exception gets thrown
	 * 
	 * I should probably create a database to simply test everything
	 */
	
	public MALDatabase(String database) throws InterruptedException {
		try {
			this.conn = DriverManager.getConnection("jdbc:sqlite:" + database);
		} catch (SQLException e) {
			System.out.println("Failed to connect to database");
			e.printStackTrace();
		}
		this.populator = new MALDatabasePopulator(conn);
		this.dropper = new MALDatabaseDropper(conn);
	}
	public void createTables() {
		try {
			Statement createTable = conn.createStatement();
			// Set encoding to UTF-8
			createTable.execute("PRAGMA encoding = 'UTF-8';");
			// Create table for people
			createTable.execute("CREATE TABLE IF NOT EXISTS people ("
					+ "id INTEGER, first_name TEXT, last_name TEXT, "
					+ "given_name TEXT, family_name TEXT, birthday TEXT, link TEXT NOT NULL, "
					+ "PRIMARY KEY(id), UNIQUE(link)"
					+ ");");
			// Create table for shows
			createTable.execute("CREATE TABLE IF NOT EXISTS shows ("
					+ "id INTEGER, romanized_title TEXT NOT NULL, japanese_title TEXT, "
					+ "english_title TEXT, premiere_term TEXT, premiere_year INTEGER, "
					+ "type TEXT NOT NULL, link TEXT NOT NULL, "
					+ "PRIMARY KEY(id), UNIQUE(link)"
					+ ");");
			// Create table for characters
			createTable.execute("CREATE TABLE IF NOT EXISTS characters ("
					+ "id INTEGER, name TEXT NOT NULL, link TEXT NOT NULL, "
					+ "PRIMARY KEY(id), UNIQUE(link)"
					+ ");");
			
			// Create table for voice_actors
			createTable.execute("CREATE TABLE IF NOT EXISTS voice_actors ("
					+ "va_id INTEGER, show_id INTEGER, "
					+ "FOREIGN KEY (va_id) REFERENCES people(id),"
					+ "FOREIGN KEY (show_id) REFERENCES shows(id)"
					+ ");");
			// Create table for staff
			createTable.execute("CREATE TABLE IF NOT EXISTS staff ("
					+ "staff_id INTEGER, show_id INTEGER, position TEXT, "
					+ "FOREIGN KEY (staff_id) REFERENCES people(id),"
					+ "FOREIGN KEY (show_id) REFERENCES shows(id)"
					+ ");");
			// Create table for roles
			createTable.execute("CREATE TABLE IF NOT EXISTS roles ("
					+ "va_id INTEGER, char_id INTEGER, show_id INTEGER, "
					+ "FOREIGN KEY (va_id) REFERENCES people(id),"
					+ "FOREIGN KEY (char_id) REFERENCES characters(id)"
					+ ");");
			// Create table for shows_chars
			createTable.execute("CREATE TABLE IF NOT EXISTS shows_chars ("
					+ "show_id INTEGER, char_id INTEGER, "
					+ "FOREIGN KEY (show_id) REFERENCES shows(id)"
					+ "FOREIGN KEY (char_id) REFERENCES characters(id)"
					+ ");");
		} catch (SQLException e) {
			System.out.println("Failed to create tables");
			e.printStackTrace();
		}
	}
	public void dropTables() {
		try {
			Statement createTable = conn.createStatement();
			// Set encoding to UTF-8
			// Drop table for people
			createTable.execute("DROP TABLE IF EXISTS people;");
			// Drop table for shows
			createTable.execute("DROP TABLE IF EXISTS shows;");
			// Drop table for characters
			createTable.execute("DROP TABLE IF EXISTS characters;");
			// Drop table for voice_actors
			createTable.execute("DROP TABLE IF EXISTS voice_actors;");
			// Drop table for staff
			createTable.execute("DROP TABLE IF EXISTS staff;");
			// Drop table for roles
			createTable.execute("DROP TABLE IF EXISTS roles;");
			// Drop table for roles
			createTable.execute("DROP TABLE IF EXISTS shows_chars;");

		} catch (SQLException e) {
			System.out.println("Failed to drop tables");
			e.printStackTrace();
		}
	}
	public boolean hasTables() {
		int tableCount = 0;
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet result = dbmd.getTables(null, null, "%", null);
			while (result.next()) {
				tableCount++;
			}
		} catch (SQLException e) {
			System.out.println("Failed to query database");
			e.printStackTrace();
		}
		if (tableCount == TABLE_NAMES.length) {
			return true;
		}
		return false;
	}
	
	public boolean populateTables() {
		boolean populated = false;
		try {
			populated = populator.populate();
		} catch (Exception e) {
			System.out.println("Error occurred");
			try {
				deleteSeason();
			} catch (Exception ex) {
				e.printStackTrace();
			}
			e.printStackTrace();
			populated = false;
		}
		System.out.println("Population successful: " + populated);
		return populated;
	}
	private void deleteSeason() throws FileNotFoundException, InterruptedException {
		SeasonPage season = null;
		Scanner fileScan = new Scanner(new File(FILE));
		String seasonURL = fileScan.nextLine();
		fileScan.close();
		
		while (season == null) {
			try {
				season = new SeasonPage(seasonURL);
			} catch (HttpStatusException e) {
				Thread.sleep(30000);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		dropper.dropSeason(season.getSeason());
	}
	public boolean insertSeason(Season season) {
		try {
			populator.insertSeason(season);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean deleteSeason(Season season) {
		return dropper.dropSeason(season);
	}
}
