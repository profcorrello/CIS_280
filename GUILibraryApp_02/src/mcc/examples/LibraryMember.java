package mcc.examples;

public class LibraryMember {

	private int memberId;
	private String libraryCard;
	private String firstName;
	private String lastName;
	private String email;
	
	public LibraryMember(int memberId, String libraryCard, String firstName, String lastName, String email) {		
		this.memberId = memberId;
		this.libraryCard = libraryCard;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getLibraryCard() {
		return libraryCard;
	}

	public void setLibraryCard(String libraryCard) {
		this.libraryCard = libraryCard;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(this.libraryCard);
		sb.append(" ");
		sb.append(this.firstName + " " + this.getLastName());
		sb.append(" ");
		sb.append(this.email);

		return sb.toString();
	}

	

}
