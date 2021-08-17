package scrapingmal;

public class CharacterPage extends Page {
	private String name;

	public CharacterPage(String url) throws Exception {
		super(url);
		if (url.indexOf("https://myanimelist.net/character/") != 0) {
			throw new Exception("Pass URL with domain https://myanimelist.net/character/");
		}
		scrapeData();
	}
	public String getName() {
		return name;
	}
	@Override
	protected void scrapeData() {
		this.name = getPage().selectFirst("strong").html();
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public int hashCode() {
		return getPage().hashCode();
	}
}
