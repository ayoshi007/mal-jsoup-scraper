package scrapingmal.helpers;

public enum AnimeType {
	TV ("TV"),
	ONA ("ONA"),
	OVA ("OVA"),
	MOVIE ("Movie"),
	SPECIAL ("Special");
	
	String type;
	
	private AnimeType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type;
	}
}
