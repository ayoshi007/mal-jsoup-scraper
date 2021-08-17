package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import scrapingmal.helpers.Season;

public class MALDatabaseDropper {
	String str = "DELETE FROM shows WHERE id = ?;";
	// NEED TO CHANGE STATEMENTS
	private static final String DELETE_SHOW = 
			"DELETE FROM shows WHERE id = ?;";
		
	private static final String DELETE_CHARACTER = 
			"DELETE FROM characters WHERE id = ?;";
		
	private static final String DELETE_PERSON = 
			"DELETE FROM people WHERE id = ?;";
		
	private static final String DELETE_VOICE_ACTOR = 
			"DELETE FROM voice_actors WHERE show_id = ?;";
		
	private static final String DELETE_ROLE = 
			"DELETE FROM roles WHERE show_id = ?;";
		
	private static final String DELETE_SHOW_CHAR = 
			"DELETE FROM shows_chars WHERE show_id = ?;";
		
	private static final String DELETE_STAFF = 
			"DELETE FROM staff WHERE show_id = ?;";
		
	private static final String FIND_CHAR_ID = 
			"SELECT id FROM characters WHERE link = ?;";
		
	private static final String FIND_PERSON_ID = 
			"SELECT id FROM people WHERE link = ?;";
	
	private static final String FIND_SEASON = 
			"SELECT id FROM shows WHERE premiere_term = ? AND premiere_year = ?;";
	
	private Connection conn;
	
	public MALDatabaseDropper(Connection conn) {
		this.conn = conn;
	}
	
	public boolean dropShow(int showId) {
		try (PreparedStatement dropShow = conn.prepareStatement(DELETE_SHOW)) {
			boolean droppedVAs = dropVAsOfShow(showId);
			boolean droppedRoles = dropRolesOfShow(showId);
			boolean droppedShowChars = dropShowCharsOfShow(showId);
			boolean droppedStaff = dropStaffOfShow(showId);
			
			dropShow.setInt(1, showId);
			boolean droppedShow = dropShow.executeUpdate() == 0;
			
			return droppedVAs && droppedRoles && droppedShowChars && droppedStaff && droppedShow;
		} catch (SQLException e) {
			System.out.println("Failed to drop show");
			e.printStackTrace();
			return false;
		}
	}
	private boolean dropVAsOfShow(int showId) throws SQLException {
		PreparedStatement dropVAs = conn.prepareStatement(DELETE_VOICE_ACTOR);
		dropVAs.setInt(1, showId);
		return dropVAs.executeUpdate() == 0;
	}
	private boolean dropRolesOfShow(int showId) throws SQLException {
		PreparedStatement dropRoles = conn.prepareStatement(DELETE_ROLE);
		dropRoles.setInt(1, showId);
		return dropRoles.executeUpdate() == 0;
	}
	private boolean dropShowCharsOfShow(int showId) throws SQLException {
		PreparedStatement dropShowChars = conn.prepareStatement(DELETE_SHOW_CHAR);
		dropShowChars.setInt(1, showId);
		return dropShowChars.executeUpdate() == 0;
	}
	private boolean dropStaffOfShow(int showId) throws SQLException {
		PreparedStatement dropStaff = conn.prepareStatement(DELETE_STAFF);
		dropStaff.setInt(1, showId);
		return dropStaff.executeUpdate() == 0;
	}
	
	// IMPLEMENT HERE
	public boolean dropSeason(Season season) {
		String term = season.getTerm().toString();
		int year = season.getYear();
		try (PreparedStatement selectSeasonAnime = conn.prepareStatement(FIND_SEASON, Statement.RETURN_GENERATED_KEYS)) {
			selectSeasonAnime.setString(1, term);
			selectSeasonAnime.setInt(2, year);
			ResultSet showIds = selectSeasonAnime.executeQuery();
			while (showIds.next()) {
				if (dropShow(showIds.getInt(1))) {
					return false;
				}
			}
		} catch (SQLException e) {
			System.out.println("Failed to select seasonal anime");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
