package scrapingmal.helpers;

public class Person {
	private String firstName;
	private String lastName;
	private String givenName;
	private String familyName;
	private Birthday birthday;
	
	public Person(String firstName, String lastName) {
		this(firstName, lastName, "Unknown");
	}
	
	public Person(String firstName, String lastName, String bday) {
		this(firstName, lastName, null, null, bday);
	}
	
	public Person(String firstName, String lastName, String givenName, String familyName, String bday) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.givenName = givenName;
		this.familyName = familyName;
		this.birthday = new Birthday(bday);
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getGivenName() {
		return givenName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public Birthday getBirthday() {
		return birthday;
	}
	@Override
	public String toString() {
		if (givenName == null || familyName == null) {
			return String.format(	"%s%s\n"
								+ 	"Born: %s",
									firstName.isEmpty() ? "": firstName + " ", lastName, birthday.toString());
		}
		return String.format(	"%s %s, eng. %s %s\n"
							+ 	"Born: %s",
								familyName, givenName, firstName, lastName, birthday.toString());
	}
}
