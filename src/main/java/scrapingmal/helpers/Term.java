package scrapingmal.helpers;

public enum Term {
	SPRING ("Spring"),
	SUMMER ("Summer"),
	WINTER ("Winter"),
	FALL ("Fall");
	
	private final String term;
	private Term(String term) {
		this.term = term;
	}
	public String getTerm() {
		return term;
	}
	@Override
	public String toString() {
		return term;
	}
}
