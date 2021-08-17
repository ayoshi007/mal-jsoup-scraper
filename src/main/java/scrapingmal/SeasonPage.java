package scrapingmal;

import scrapingmal.helpers.*;

import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SeasonPage extends Page {
	private Season season;
	private ArrayList<String> urls;
	
	public SeasonPage(String url) throws Exception {
		super(url);
		if (url.indexOf("https://myanimelist.net/anime/season") != 0) {
			throw new Exception("Pass URL with domain https://myanimelist.net/anime/season");
		}
		this.urls = new ArrayList<>();
		scrapeSeason();
		scrapeData();
	}
	private void scrapeSeason() {
		String[] seasonHtml = getPage().getElementsByClass("season_nav").get(0).child(0).html().split(" ");
		this.season = new Season(seasonHtml[0].toUpperCase(), Integer.parseInt(seasonHtml[1]));
	}
	@Override
	protected void scrapeData() {
		scrapeURLs();
		//scrapeAnimePages();
	}
	
	private void scrapeURLs() {
		// scrape using class seasonal-anime-list
		// scrape only one AnimePage at a time and query into SQL db as you go
		Elements showData = getPage().getElementsByClass("seasonal-anime-list");
		for (Element element: showData) {
			if (!element.child(0).html().contains("TV (Continuing)")) {
				Elements shows = element.getElementsByClass("seasonal-anime");
				for (Element show: shows) {
					try {
						//Thread.sleep(2000);
						String animeURL = show.getElementsByClass("link-title").get(0).attr("abs:href");
						urls.add(animeURL);
						//System.out.println(animeURL);
					} catch (Exception e) {
						System.out.println("Failed to scrape anime");
						e.printStackTrace();
					}
				}
			}
		}
	}
	private void scrapeAnimePages() {
		for (String url: urls) {
			try {
				AnimePage animePage = new AnimePage(url);
				animePage.setPremiereSeason(season);
				System.out.println(animePage);
			} catch (Exception e) {
				System.out.println("Failed to scrape anime page");
			}
			
		}
	}
	public Season getSeason() {
		return season;
	}
	public int getScrapedPagesAmount() {
		return urls.size();
	}
	public ArrayList<String> getURLs() {
		return urls;
	}

}
