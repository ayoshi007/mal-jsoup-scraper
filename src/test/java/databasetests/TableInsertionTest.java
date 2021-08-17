package databasetests;

import static org.junit.Assert.*;

import org.junit.Test;

import database.MALDatabase;
import scrapingmal.helpers.Season;
import scrapingmal.helpers.Term;

public class TableInsertionTest {

	
	@Test
	public void testTableCreation() {
		try {
			MALDatabase testdb = new MALDatabase("testerdb.db");
			testdb.dropTables();
			testdb.createTables();
			assertTrue(testdb.hasTables());
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
			fail();
		}
		return;
	}
	@Test
	public void testSeasonInsertion() {
		try {
			MALDatabase testdb = new MALDatabase("testerdb.db");
			assertTrue(testdb.insertSeason(new Season(Term.WINTER, 2021)));
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
			fail();
		}
	}
	/*
	@Test
	public void testTablePopulation() {
		try {
			MALDatabase testdb = new MALDatabase("testerdb.db");
			if (testdb.hasTables()) {
				assertTrue(testdb.populateTables());
			} else {
				fail();
			}
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
			fail();
		}
		return;
	}
	*/
	
	/*
	@Test
	public void testSeasonDeletion() {
		try {
			MALDatabase testdb = new MALDatabase("testerdb.db");
			assertTrue(testdb.deleteSeason(new Season(Term.WINTER, 2021)));
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
			fail();
		}
	}
	*/
}