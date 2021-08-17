package scrapingmal;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import scrapingmal.helpers.Season;
import scrapingmal.helpers.AnimeType;
import scrapingmal.helpers.Term;

public class AnimePage extends Page {
	private String romanizedTitle;
	private String englishTitle;
	private String japaneseTitle;
	private Season premiereSeason;
	//private AnimeStaffPage staff;
	private String staffURL;
	private AnimeType type;
	
	public AnimePage(String url) throws Exception {
		super(url);
		if (url.indexOf("https://myanimelist.net/anime/") != 0) {
			throw new Exception("Pass URL with domain https://myanimelist.net/anime/");
		}
		scrapeData();
	}
	@Override
	protected void scrapeData() {
		this.romanizedTitle = scrapeRomanizedTitle();
		scrapeTitlesAndPremiereSeason();
		this.staffURL = scrapeStaffURL();
		try {
			/*
			if (staffURL != null) {
				this.staff = new AnimeStaffPage(staffURL);
			}
			*/
		} catch (Exception e) {
			System.out.println("Failed to scrape staff page");
			e.printStackTrace();
		}
	}
	private String scrapeRomanizedTitle() {
		return getPage().selectFirst("strong").html();
	}
	/*
	 * NEED TO CHANGE IMPLEMENTATION OF scrapeEnglishTitle and scrapeJapaneseTitle and scrapepremiereSeason
	 * 	- using get(i) is not flexible for cases without English titles in the HTML
	 * 	- need to combine and iterate through a list of elements with class "dark_text" instead
	 */
	private void scrapeTitlesAndPremiereSeason() {
		for (Element e: getPage().getElementsByClass("dark_text")) {
			if (e.html().equals("English:")) {
				String html = e.parent().html();
				this.englishTitle = html.substring(html.indexOf("</span>") + 8);
			} else if (e.html().equals("Japanese:")) {
				String html = e.parent().html();
				this.japaneseTitle = html.substring(html.indexOf("</span>") + 8);
			} else if (e.html().equals("Premiered:")) {
				String[] html = e.nextElementSibling().html().split(" ");
				Term term = null;
				switch(html[0]) {
					case "Spring":
						term = Term.SPRING;
						break;
					case "Summer":
						term = Term.SUMMER;
						break;
					case "Fall":
						term = Term.FALL;
						break;
					default:
						term = Term.WINTER;
				}
				this.premiereSeason = new Season(term, Integer.parseInt(html[1]));
			} else if (e.html().equals("Type:")) {
				String html = e.nextElementSibling().html();
				switch(html) {
					case "TV":
						this.type = AnimeType.TV;
						break;
					case "ONA":
						this.type = AnimeType.ONA;
						break;
					case "OVA":
						this.type = AnimeType.OVA;
						break;
					case "Movie":
						this.type = AnimeType.MOVIE;
						break;
					default:
						this.type = AnimeType.SPECIAL;
				}
			}
		}
	}
	
	private String scrapeStaffURL() {
		Elements tags = getPage().getElementsByClass("detail-characters-list");
		if (tags.size() == 0) {
			return null;
		}
		Element aTag = tags.get(0).
						previousElementSibling().
						child(0).
						selectFirst("a[href]");
		if (aTag != null) {
			String route = aTag.attr("abs:href");
			return route;
		}
		// Get the absolute href link of the <a> element
		int urlParamIndex = getURL().indexOf("?");
		if (urlParamIndex != -1) {
			return getURL().substring(0, urlParamIndex) + "/characters"; 
		}
		return getURL() + "/characters";
	}
	public String getRomanizedTitle() {
		return romanizedTitle;
	}
	public String getEnglishTitle() {
		return englishTitle;
	}
	public String getJapaneseTitle() {
		return japaneseTitle;
	}
	public Season getPremiereSeason() {
		return premiereSeason;
	}
	public void setPremiereSeason(Season season) {
		this.premiereSeason = season;
	}
	/*
	public AnimeStaffPage getStaff() {
		return staff;
	}
	*/
	public boolean hasStaff() {
		return staffURL != null;
	}
	public String getStaffURL() {
		return staffURL;
	}
	public AnimeType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		if (englishTitle == null) {
			return String.format(	"%s\n"
								+ 	"%s (%s)\n"
								+ 	"Premiered %s",
									type.toString(), japaneseTitle, romanizedTitle, premiereSeason);
		}
		return String.format(	"%s\n"
							+ 	"%s (%s), eng. %s\n"
							+ 	"Premiered %s",
							type.toString(), japaneseTitle, romanizedTitle, englishTitle, premiereSeason);
	}
}
