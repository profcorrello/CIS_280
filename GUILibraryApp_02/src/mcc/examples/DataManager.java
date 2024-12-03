package mcc.examples;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DataManager {

	private Logger logger = Logger.getLogger(DataManager.class.getName());

	private List<Book> books = new ArrayList<Book>();
	private List<LibraryMember> libraryMembers = new ArrayList<LibraryMember>();
	private List<CheckedOutBook> checkedOutBooks = new ArrayList<CheckedOutBook>();
	RulesEngine rulesEngine = new RulesEngine();

	public void resetData() {
		books.clear();
		libraryMembers.clear();
		checkedOutBooks.clear();
	}

	public List<CheckedOutBook> getCheckedOutBooks() {

		if (checkedOutBooks.isEmpty()) {
			try {
				Statement stmt = dbUtil.getConnection().createStatement();
				String sql = "SELECT MEMBER_ID, BOOK_ID, CHECK_OUT_DATE\n" + "FROM checked_out_books;";
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					CheckedOutBook checkedOutBook = new CheckedOutBook(rs.getInt("MEMBER_ID"), rs.getInt("BOOK_ID"),
							rs.getString("CHECK_OUT_DATE"));
					checkedOutBooks.add(checkedOutBook);
				}
			} catch (Exception e) {
				logger.severe("Error loading checked out books from database: " + e.getMessage());
			}
		}
		return checkedOutBooks;
	}

	private DBUtil dbUtil = new DBUtil();

	public List<Book> getBooks() {
		if (books.isEmpty()) {
			loadBooksFromDB();
		}
		return books;
	}

	private void loadBooksFromDB() {
		books.clear();

		try {
			// Create a statement
			logger.info("Connecting to database");
			Statement stmt = dbUtil.getConnection().createStatement();

			String sql = "SELECT \"Index\", \"Book Title\", \"Author First Name\", \"Author Last Name\", ISBN, CHECKED_OUT\n"
					+ "FROM books;";

			ResultSet rs = stmt.executeQuery(sql);

			// Process the result set
			while (rs.next()) {
				int index = rs.getInt("Index");
				String bookTitle = rs.getString("Book Title");
				String authorFirstName = rs.getString("Author First Name");
				String authorLastName = rs.getString("Author Last Name");
				String isbn = rs.getString("ISBN");
//				boolean checkedOut = (rs.getInt("CHECKED_OUT") != 0);
//				boolean checkedOut = false;
				boolean checkedOut = rs.getBoolean("CHECKED_OUT");

				Book book = new Book(index, bookTitle, authorFirstName, authorLastName, isbn, checkedOut);
				books.add(book);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<LibraryMember> getLibraryMembers() {
		if (libraryMembers.isEmpty()) {
			loadLibraryMembersFromDB();
		}
		return libraryMembers;
	}

	private void loadLibraryMembersFromDB() {
		libraryMembers.clear();

		try {
			// Create a statement
			logger.info("Connecting to database");
			Statement stmt = dbUtil.getConnection().createStatement();

			String sql = "SELECT member_id, library_card, first_name, last_name, email\n" + "FROM library_members;";

			ResultSet rs = stmt.executeQuery(sql);

			// Process the result set
			while (rs.next()) {
				int memberId = rs.getInt("member_id");
				String libraryCard = rs.getString("library_card");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String email = rs.getString("email");

				LibraryMember lm = new LibraryMember(memberId, libraryCard, firstName, lastName, email);
				libraryMembers.add(lm);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean checkOutBook(int library_member_id, int book_id) {
		boolean success = false;

		// check to see if the book is already checked out
		List<Book> co_books = FilterUtils.filterList(books, book -> book.getIndex() == book_id);

		if (co_books.size() > 0) {
			for (Book book : co_books) {
				if (book.isCheckedOut()) {
					logger.info(book.getBook_title() + " is already checked out.");
					return false;
				}
			}
		}

		String todaysDate = getTodaysDate();

		// TODO: get todays date for updating the checkout table
		// TODO: update the checkout table.
		// TODO: update the book table to set the checked out flag to true for the book

		String sql = "INSERT INTO checked_out_books\n" + "(MEMBER_ID, BOOK_ID, CHECK_OUT_DATE)\n" + "VALUES(?, ?, ?);";

		try {
			PreparedStatement stmt = dbUtil.getConnection().prepareStatement(sql);

			stmt.setInt(1, library_member_id);
			stmt.setInt(2, book_id);
			stmt.setString(3, todaysDate);
			stmt.executeUpdate();
			success = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		String bookSql = "UPDATE books \n" + "SET  CHECKED_OUT=1 \n" + "WHERE  \"Index\" = ?";

		try {
			PreparedStatement stmt = dbUtil.getConnection().prepareStatement(bookSql);

			stmt.setInt(1, book_id);

			stmt.executeUpdate();
			success = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return success;
	}

	public String getTodaysDate() {
		// Get the current date
		LocalDate today = LocalDate.now();

		// Define the desired date format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// Format the date as a string and return it
		return today.format(formatter);
	}

	public String getCheckedOutBooksForMember(int memberId) {
		StringBuilder sb = new StringBuilder();
		logger.info("Getting checked out books for member " + memberId);
		if (checkedOutBooks.isEmpty()) {
			logger.info("Loading checked out books from database");
			getCheckedOutBooks();
		}

		for (CheckedOutBook checkedOutBook : checkedOutBooks) {
			if (checkedOutBook.getMemberId() == memberId) {
				int bookId = checkedOutBook.getBookId();
				String checkoutDate = checkedOutBook.getCheckoutDate();
				List<Book> co_books = FilterUtils.filterList(books, book -> book.getIndex() == bookId);

				for (Book book : co_books) {
					long daysSinceCheckout = getDateDifferenceFromToday(checkoutDate);
					sb.append("<p>");
					sb.append("Book Title: " + book.getBook_title() + ",<italic> Author: " + book.getAuthorFullName()
							+ "</italic>");
					sb.append(" <strong> <span color=\"red\" >");
					sb.append("Checked Out: " + checkoutDate);
					sb.append("</span> </strong>");
					sb.append(" Days since checkout: " + daysSinceCheckout);
					if (rulesEngine.isBookOverdue(daysSinceCheckout)) {
						sb.append(" <strong> <span color=\"red\" >");
						sb.append("Overdue!");
						sb.append("</span> </strong>");
						sb.append("<p><h3>");
						sb.append(" Overdue fine: " + rulesEngine.calculateOverdueFine(daysSinceCheckout));
						sb.append("</h3></p>");
					}
					sb.append("</p>");
				}
			}
		}

		return sb.toString();
	}

	public long getDateDifferenceFromToday(String dateToCheck) {
		// Define the date formatter
		String format = detectDateFormat(dateToCheck);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

		// Parse the dates
		LocalDate parsedDate1 = LocalDate.parse(dateToCheck, formatter);
		LocalDate parsedDate2 = LocalDate.parse(getTodaysDate(), formatter);

		// Calculate the difference in days
		return ChronoUnit.DAYS.between(parsedDate1, parsedDate2);
	}

	/**
	 * Determines the format of a given date String.
	 *
	 * @param dateStr The date String to analyze.
	 * @return The matching date format as a String, or null if no format matches.
	 */
	public String detectDateFormat(String dateStr) {
		// List of common date formats
		List<String> dateFormats = List.of("yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy", "yyyy/MM/dd", "dd/MM/yyyy",
				"MMM dd, yyyy", // e.g., Nov 15, 2024
				"MMMM dd, yyyy", // e.g., November 15, 2024
				"yyyyMMdd" // e.g., 20241115
		);

		// Try parsing the date with each format
		for (String format : dateFormats) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			try {
				// Attempt to parse the date
				LocalDate.parse(dateStr, formatter);
				return format; // Return the matching format if parsing succeeds
			} catch (DateTimeParseException e) {
				// Continue trying other formats
			}
		}

		// Return null if no format matches
		return null;
	}

	public boolean isBookOverDue(Book book) {
		if (checkedOutBooks.isEmpty()) {
			logger.info("Loading checked out books from database");
			getCheckedOutBooks();
		}

		for (CheckedOutBook checkedOutBook : checkedOutBooks) {
			if (checkedOutBook.getBookId() == book.getIndex()) {
				long daysSinceCheckout = getDateDifferenceFromToday(checkedOutBook.getCheckoutDate());
				return rulesEngine.isBookOverdue(daysSinceCheckout);
			}
		}
		return false;
	}

}
