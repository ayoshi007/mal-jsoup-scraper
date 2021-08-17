package scrapingmaltests;
import static org.junit.Assert.*;

import org.junit.Test;

import scrapingmal.AnimeStaffPage;

public class AnimeStaffTest {
	/*
	@Test
	public void testNausicaaStaffScrape() throws Exception {
		AnimeStaffPage nausicaaStaff = new AnimeStaffPage("https://myanimelist.net/anime/572/Kaze_no_Tani_no_Nausica%C3%A4/characters");
		assertTrue(nausicaaStaff.getRoles().size() == 15);
		assertTrue(nausicaaStaff.getStaff().size() == 28);
	}
	*/
	@Test
	public void testFMABStaffScrape() throws Exception {
		AnimeStaffPage fmaB = new AnimeStaffPage("https://myanimelist.net/anime/5114/Fullmetal_Alchemist__Brotherhood/characters");
		assertTrue(fmaB.getRolesURLs().size() + fmaB.getStaffURLs().size() == 240);
	}
}