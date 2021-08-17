package scrapingmaltests;

import static org.junit.Assert.*;

import org.junit.Test;

import scrapingmal.SeasonPage;

public class SeasonPageTest {

	@Test
	public void testWinter2000Scrape() throws Exception {
		SeasonPage winter2000 = new SeasonPage("https://myanimelist.net/anime/season/2000/winter");
		System.out.println("2000年冬スクレープ完了");
		assertEquals(40, winter2000.getURLs().size());
	}
	@Test
	public void testFall1917Scrape() throws Exception {
		SeasonPage winter2000 = new SeasonPage("https://myanimelist.net/anime/season/1917/fall");
		System.out.println("1917年秋スクレープ完了");
		assertEquals(4, winter2000.getURLs().size());
	}
	@Test
	public void testWinter2005Scrape() throws Exception {
		SeasonPage winter2000 = new SeasonPage("https://myanimelist.net/anime/season/2005/winter");
		System.out.println("2005年冬スクレープ完了");
		assertEquals(76, winter2000.getURLs().size());
	}
}
