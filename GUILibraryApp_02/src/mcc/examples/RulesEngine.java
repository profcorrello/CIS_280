package mcc.examples;

import java.util.logging.Logger;

public class RulesEngine {
	private Logger logger = Logger.getLogger(RulesEngine.class.getName());
	private static final long CHECKOUT_LENGTH = 1;
	private static final int MAX_BOOKS_PER_MEMBER = 5;
	private static final float OVERDUE_FINE_PER_DAY = 1.23f;
	
	public String calculateOverdueFine(long daysSinceCheckout) {
		
		int daysOverdue = daysBookIsOverdue(daysSinceCheckout);
		// Calculate the fine based on the number of days overdue
        float fine = daysOverdue * OVERDUE_FINE_PER_DAY;
        return String.format("$%.2f", fine);
	}

	public boolean isEligibleForCheckout(Book book) {

		// Check if the book is checked out by another member
		if (book.isCheckedOut()) {
			logger.info("Book is already checked out. Cannot checkout the book.");
			return false;
		}
		return true;

	}

	public boolean isBookOverdue(long daysCheckedOut) {

		// Check if the book has exceeded the checkout length
		if (daysCheckedOut > CHECKOUT_LENGTH) {
			logger.info("Book is overdue. Cannot checkout the book.");
			return true;
		}

		return false;

	}
	
	public int daysBookIsOverdue(long daysCheckedOut) {
		// Calculate the number of days overdue
        return (int) (daysCheckedOut - CHECKOUT_LENGTH);
	}
}
