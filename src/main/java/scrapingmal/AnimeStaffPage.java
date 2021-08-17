package scrapingmal;
import java.util.HashMap;
import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnimeStaffPage extends Page {
	/*
	private HashMap<CharacterPage, ArrayList<PersonPage>> roles;
	private HashMap<PersonPage, String> staff;
	*/
	// CharacterPage link with PersonPage links
	private HashMap<String, ArrayList<String>> rolesURLs;
	// PersonPage link with position
	private HashMap<String, String> staffURLs;
	
	public AnimeStaffPage(String url) throws Exception {
		super(url);
		if (url.indexOf("https://myanimelist.net/anime") != 0) {
			throw new Exception("Pass URL with domain https://myanimelist.net/anime");
		}
		/*
		roles = new HashMap<>();
		staff = new HashMap<>();
		*/
		rolesURLs = new HashMap<>();
		staffURLs = new HashMap<>();
		scrapeData();
	}
	@Override
	protected void scrapeData() {
		// TODO Auto-generated method stub
		Elements charsAndStaff = getPage().getElementsByClass("border_solid");
		try {
			for (int i = 0; i < charsAndStaff.size(); i++) {
				String html = charsAndStaff.get(i).getElementsByTag("h2").get(0).html();
				if (html.equals("Characters &amp; Voice Actors")) {
					scrapeVoiceActorsAndCharacters(charsAndStaff.get(i).nextElementSibling());
				}
				else if (html.equals("Staff")) {
					scrapeStaff(charsAndStaff.get(i).nextElementSibling());
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to scrape characters and staff");
			e.printStackTrace();
		}
	}
	private void scrapeVoiceActorsAndCharacters(Element charTable) throws Exception {
		for ( ; charTable.tagName().equals("table"); charTable = charTable.nextElementSibling()) {
			Elements charData = charTable.selectFirst("tr").children();
			
			String charURL = charData.get(1).select("a[href]").attr("abs:href");
			//CharacterPage character = new CharacterPage(charURL);
			//System.out.println(character);
			//System.out.println("Played by:");
			
			//ArrayList<PersonPage> voiceActors = new ArrayList<>();
			ArrayList<String> vaURLs = new ArrayList<>();
			
			Element voiceActorData = charData.get(2).selectFirst("tbody");
			if (voiceActorData != null) {
				for (Element e: voiceActorData.children()) {
					//System.out.println(e.selectFirst("a[href]").html());
					String vaURL = e.selectFirst("a[href]").attr("abs:href");
					vaURLs.add(vaURL);
					//voiceActors.add(new PersonPage(vaURL));
				}
			}
			//roles.put(character, voiceActors);
			rolesURLs.put(charURL, vaURLs);
			//System.out.println();
		}
	}
	private void scrapeStaff(Element staffTable) throws Exception {
		for ( ; staffTable != null; staffTable = staffTable.nextElementSibling()) {
			Element parentTR = staffTable.selectFirst("tr").children().get(1);
			String staffURL = parentTR.select("a[href]").attr("abs:href");
			//PersonPage staffMember = new PersonPage(staffURL);
			String position = parentTR.selectFirst("small").html();
			//staff.put(staffMember, position);
			staffURLs.put(staffURL, position);
		}
	}
	/*
	public HashMap<CharacterPage, ArrayList<PersonPage>> getRoles() {
		return roles;
	}
	public HashMap<PersonPage, String> getStaff() {
		return staff;
	}
	*/
	public HashMap<String, ArrayList<String>> getRolesURLs() {
		return rolesURLs;
	}
	public HashMap<String, String> getStaffURLs() {
		return staffURLs;
	}
}
