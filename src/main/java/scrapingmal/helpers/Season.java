package scrapingmal.helpers;

public class Season {
	private Term term;
	private int year;
	
	public Season(Term term, int year) {
		this.term = term;
		this.year = year;
	}
	public Season(String term, int year) {
		switch (term) {
			case "WINTER":
				this.term = Term.WINTER;
				break;
			case "SPRING":
				this.term = Term.SPRING;
				break;
			case "SUMMER":
				this.term = Term.SUMMER;
				break;
			case "FALL":
				this.term = Term.FALL;
				break;
		}
		this.year = year;
	}
	
	public Term getTerm() {
		return term;
	}
	public int getYear() {
		return year;
	}
	
	public String toString() {
		return term.toString() + " " + year;
	}
	public boolean equals(Object o) {
		if (o instanceof Season) {
			Season other = (Season) o;
			if (this.term.toString().equals(other.term.toString()) && this.year == other.year) {
				return true;
			}
		}
		return false;
	}
}
