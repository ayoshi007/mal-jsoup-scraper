package main;

import database.MALDatabase;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		MALDatabase testdb = new MALDatabase("test.db");
		testdb.createTables();
		testdb.populateTables();
	}
}