package scrapingmaltests;

import static org.junit.Assert.*;

import org.junit.Test;

import scrapingmal.ArchivePage;

public class ArchivePageTest {

	@Test
	public void testArchivePageScrape() throws Exception {
		ArchivePage archive = new ArchivePage();
		System.out.println("スクレープしたリンク数：　" + archive.getNumberOfSeasons());
		assertEquals(421, archive.getNumberOfSeasons());
	}
}