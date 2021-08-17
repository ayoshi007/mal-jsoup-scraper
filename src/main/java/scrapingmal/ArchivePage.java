package scrapingmal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArchivePage extends Page {
	private ArrayList<String> seasonURLs;
	private int startIndex;

	public ArchivePage() throws Exception {
		this("https://myanimelist.net/anime/season/archive");
	}
	public ArchivePage(int startIndex) throws Exception {
		this();
		this.startIndex = startIndex;
	}
	public ArchivePage(String url) throws Exception {
		super(url);
		if (url.indexOf("https://myanimelist.net/anime/season/archive") != 0) {
			throw new Exception("Pass URL with domain https://myanimelist.net/anime/season/");
		}
		this.seasonURLs = new ArrayList<>();
		this.startIndex = 0;
		scrapeData();
	}

	@Override
	protected void scrapeData() {
		try {
			Elements seasonLinks = getPage().getElementsByClass("anime-seasonal-byseason").get(0).select("a[href]");
			for (Element link: seasonLinks) {
				seasonURLs.add(link.attr("abs:href"));
			}
		} catch (Exception e) {
			System.out.println("Failed to scrape archive");
			e.printStackTrace();
		}
	}
	public ArrayList<String> getSeasonURLs() {
		return seasonURLs;
	}
	public int getNumberOfSeasons() {
		return seasonURLs.size();
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int index) {
		startIndex = index;
	}
	public boolean storeIndex() {
		try (PrintWriter writer = new PrintWriter(new File("index.txt"))) {
			writer.write(startIndex);
		} catch (IOException e) {
			System.out.println("Failed to write to index.txt");
			return false;
		}
		return true;
	}
}
