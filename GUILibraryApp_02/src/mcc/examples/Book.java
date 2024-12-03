package mcc.examples;

public class Book {
	
	private int index;
	private String book_title;
	private String author_first_name;
	private String author_last_name;
	private String isbn;
	private boolean checkedOut;
	
	public Book(int index, String book_title, String author_first_name, String author_last_name, String isbn, boolean checked_out) {
		super();
		this.index = index;
		this.book_title = book_title;
		this.author_first_name = author_first_name;
		this.author_last_name = author_last_name;
		this.isbn = isbn;
		this.checkedOut = checked_out;
	}


	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getBook_title() {
		return book_title;
	}
	public void setBook_title(String book_title) {
		this.book_title = book_title;
	}
	public String getAuthor_first_name() {
		return author_first_name;
	}
	public void setAuthor_first_name(String author_first_name) {
		this.author_first_name = author_first_name;
	}
	public String getAuthor_last_name() {
		return author_last_name;
	}
	public void setAuthor_last_name(String author_last_name) {
		this.author_last_name = author_last_name;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public boolean isCheckedOut() {
		return checkedOut;
	}
	public void setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.book_title);
		sb.append(" ");
		sb.append(this.isbn);
		sb.append(" ");
		sb.append(this.checkedOut);		
		
		return sb.toString();
	}


	public String getAuthorFullName() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.author_first_name).append(" ").append(this.author_last_name);
		
		return sb.toString();
	}


	
}
