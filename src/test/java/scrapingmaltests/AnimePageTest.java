package scrapingmaltests;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import scrapingmal.AnimePage;
import scrapingmal.helpers.Season;
import scrapingmal.helpers.Term;

public class AnimePageTest {
	/*
	@Test
	public void testHokutoAnimePageObjectString() {
		try {
			AnimePage hokuto = new AnimePage("https://myanimelist.net/anime/967/Hokuto_no_Ken?q=hokuto&cat=anime");
			String toString = hokuto.toString();
			assertEquals(toString, String.format(toString, 	"北斗の拳 (Hokuto no Ken), eng. Fist of the North Star\n"
														+ 	"Premiered Fall 1984"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in establishing connection to URL");
		}
	}
	@Test
	public void testOnePieceAnimePageObjectString() {
		try {
			AnimePage hokuto = new AnimePage("https://myanimelist.net/anime/21/One_Piece");
			String toString = hokuto.toString();
			assertEquals(toString, String.format(toString, 	"ONE PIECE (One Piece), eng. One Piece\n"
														+ 	"Premiered Fall 1999"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in establishing connection to URL");
		}
	}
	
	static AnimePage monogatariSecondSeason;
	
	@Before
	public void initMonogatariSecondSeason() throws Exception {
		monogatariSecondSeason = new AnimePage("https://myanimelist.net/anime/17074/Monogatari_Series__Second_Season?q=monogatari&cat=anime");
	}
	@Test
	public void testMonoRomanizedTitleScrape() {
		assertTrue(monogatariSecondSeason.getRomanizedTitle().equals("Monogatari Series: Second Season"));
	}
	@Test
	public void testMonoEnglishTitleScrape() {
		assertTrue(monogatariSecondSeason.getEnglishTitle().equals("Monogatari Series: Second Season"));
	}
	@Test
	public void testMonoJapaneseTitleScrape() {
		assertTrue(monogatariSecondSeason.getJapaneseTitle().equals("〈物語〉シリーズ セカンドシーズン"));
	}
	@Test
	public void testMonoPremiereScrape() {
		assertTrue(monogatariSecondSeason.getPremiereSeason().equals(new Season(Term.SUMMER, 2013)));
	}
	static AnimePage gingaTetsudou999;
	
	@Before
	public void initGingaTetsudou999() throws Exception {
		gingaTetsudou999 = new AnimePage("https://myanimelist.net/anime/1491/Ginga_Tetsudou_999?q=ginga%20tetsudou%20999&cat=anime");
	}
	@Test
	public void test999RomanizedTitleScrape() {
		assertTrue(gingaTetsudou999.getRomanizedTitle().equals("Ginga Tetsudou 999"));
	}
	@Test
	public void test999EnglishTitleScrape() {
		assertTrue(gingaTetsudou999.getEnglishTitle() == null);
	}
	@Test
	public void test999JapaneseTitleScrape() {
		assertTrue(gingaTetsudou999.getJapaneseTitle().equals("銀河鉄道９９９"));
	}
	@Test
	public void test999PremiereScrape() {
		assertTrue(gingaTetsudou999.getPremiereSeason().equals(new Season(Term.FALL, 1978)));
	}
	@AfterClass
	public static void print999() {
		System.out.println(monogatariSecondSeason);
		System.out.println(gingaTetsudou999);
	}
	*/
	@Test 
	public void testBoogiePop() {
		try {
			AnimePage boogiePop = new AnimePage("https://myanimelist.net/anime/369/Boogiepop_wa_Warawanai");
			System.out.println(boogiePop);
			assertEquals(boogiePop.getJapaneseTitle(), "ブギーポップは笑わない Boogiepop Phantom");
		}
		catch (Exception e) {
			System.out.println("Failed to scrape page");
			e.printStackTrace();
		}
	}
	
	@Test 
	public void testSangoNoUmiNoOuji() {
		try {
			AnimePage sangoNoUmiNoOuji = new AnimePage("https://myanimelist.net/anime/10077/Sango_no_Umi_to_Ouji");
			sangoNoUmiNoOuji.setPremiereSeason(new Season(Term.WINTER, 2000));
			System.out.println(sangoNoUmiNoOuji);
			assertEquals(sangoNoUmiNoOuji.getJapaneseTitle(), "サンゴの海と王子");
		}
		catch (Exception e) {
			System.out.println("Failed to scrape page");
			e.printStackTrace();
		}
	}
}
