package scrapingmal;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

public abstract class Page {
	private String url;
	private Document page;
	
	public Page(String url) throws Exception {
		this.url = url;
		try {
			Thread.sleep(100);
			Connection response = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");
					//.execute();
			this.page = response.get();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
	// NEED TO IMPLEMENT ANIMESTAFF SCRAPING TO CHECK IF NO STAFF LINK EXISTS
	protected abstract void scrapeData();
	
	public String getURL() {
		return url;
	}
	public Document getPage() {
		return page;
	}
}