package scrapingmaltests;

import static org.junit.Assert.*;

import org.junit.Test;

import scrapingmal.PersonPage;

public class PersonPageTest {

	@Test
	public void testMiyauchiKouheiScrape() throws Exception {
		PersonPage miyauchiKouhei = new PersonPage("https://myanimelist.net/people/9188/Kouhei_Miyauchi");
		assertEquals(String.format(	"%s %s, eng. %s %s\n"
								+ 	"Born: %s",
									"宮内", "幸平", "Kouhei", "Miyauchi", "Aug 4, 1929"),
					miyauchiKouhei.toString());
	}
	@Test
	public void testManfredErdmannScrape() throws Exception {
		PersonPage manfredErdmann = new PersonPage("https://myanimelist.net/people/45047/Manfred_Erdmann");
		assertEquals(String.format(	"%s %s\n"
								+ 	"Born: %s",
									"Manfred", "Erdmann", "1939"),
				manfredErdmann.toString());
	}
	@Test
	public void testKamiyaHiroshi() throws Exception {
		PersonPage hiroC = new PersonPage("https://myanimelist.net/people/118/Hiroshi_Kamiya?q=kamiya&cat=person");
		assertEquals(hiroC.toString(),  "神谷 浩史, eng. Hiroshi Kamiya\n"
										+ 	"Born: Jan 28, 1975");
	}

}
