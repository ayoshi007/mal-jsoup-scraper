package scrapingmal.helpers;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Birthday {
	private String month;
	private int day;
	private int year;
	
	public Birthday(String bday) {
		String[] data = bday.replace(",", "").split(" ");
		if (data.length == 1) {
			if (!data[0].equals("Unknown")) {
				year = Integer.parseInt(data[0]);
			}
		} else if (data.length == 2) {
			month = data[0];
			day = Integer.parseInt(data[1]);
		}
		else {
			month = data[0];
			day = Integer.parseInt(data[1]);
			year = Integer.parseInt(data[2]);
		}
	}
	
	public Date getSQLDate() {
		try {
			return new Date(new SimpleDateFormat("MMM d, yyyy").parse(this.toString()).getTime());
		} catch (ParseException e) {
			System.out.println("Parsing exception");
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		if (month == null && day == 0 && year == 0) {
			return "Unknown";
		} else if (month == null && day == 0) {
			return Integer.toString(year);
		} else if (year == 0) {
			return month + " " + day;
		}
		return month + " " + day + ", " + year;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Birthday) {
			Birthday bday = (Birthday) o;
			if (this.month.equals(bday.month) && this.day == bday.day && this.year == bday.year) {
				return true;
			}
		}
		return false;
	}
}
