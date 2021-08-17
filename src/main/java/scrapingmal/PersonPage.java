package scrapingmal;

import org.jsoup.nodes.Element;

import scrapingmal.helpers.Person;

public class PersonPage extends Page {
	private Person person;
	
	public PersonPage(String url) throws Exception {
		super(url);
		if (url.indexOf("https://myanimelist.net/people/") != 0) {
			throw new Exception("Pass URL with domain https://myanimelist.net/people/");
		}
		scrapeData();
	}
	protected void scrapeData() {
		String[] engName = getPage().selectFirst("strong").html().split(", ");
		if (engName.length == 1) {
			engName = new String[]{engName[0], ""};
		}
		String givenName = null;
		String familyName = null;
		String bday = "Unknown";
		
		for (Element e: getPage().getElementsByClass("dark_text")) {
			if (e.html().equals("Given name:")) {
				String html = e.parent().html();
				int nameIndex = html.indexOf("</span>") + 8;
				if (nameIndex < html.length()) {
					givenName = html.substring(nameIndex);
				}
			} else if (e.html().equals("Family name:")) {
				String html = e.parent().html();
				int nameIndex = html.indexOf("Family name:</span>") + 20;
				int endIndex = html.indexOf("<div", nameIndex);
				if (nameIndex < html.length()) {
					familyName = html.substring(nameIndex, endIndex - 1);
				}
			} else if (e.html().equals("Birthday:")) {
				String html = e.parent().html();
				bday = html.substring(html.indexOf("</span>") + 8);
			}
		}
		this.person = new Person(engName[1], engName[0], givenName, familyName, bday);
	}
	public Person getPerson() {
		return person;
	}
	@Override
	public String toString() {
		return person.toString();
	}
	@Override
	public int hashCode() {
		return getPage().hashCode();
	}
}
