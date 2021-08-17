package scrapingmaltest.helpertests;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scrapingmal.helpers.Person;

public class PersonTest {
	static Person hiroC;
	static Person liamOBrien;
	static Person sungwonCho;
	static Person oSeyeong;

	@BeforeClass
	public static void init() {
		hiroC = new Person("Hiroshi", "Kamiya", "浩�?�", "神谷", "Jan 28, 1975");
		liamOBrien = new Person("Liam", "O'Brien", "May 28, 1976");
		sungwonCho = new Person("SungWon", "Cho", "Dec 9, 1990");
		oSeyeong = new Person("O", "Seyeong");
	}
	@Test
	public void testKamiyaHiroshiPerson() {
		assertEquals(hiroC.toString(),  "神谷 浩�?�, eng. Hiroshi Kamiya\n"
									+ 	"Born: Jan 28, 1975");
	}
	@Test
	public void testBirthdaySQLDate() {
		assertEquals(hiroC.getBirthday().getSQLDate().getTime(), 160120800000L);
	}
	@AfterClass
	public static void printPeople() {
		System.out.println(hiroC);
		System.out.println(liamOBrien);
		System.out.println(sungwonCho);
		System.out.println(oSeyeong);
	}
}
