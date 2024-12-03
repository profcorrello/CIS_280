package mcc.examples;

public class CheckedOutBook {

    private int memberId;
    private int bookId;
    private String checkoutDate;

 

	public CheckedOutBook(int memberId, int bookId, String checkoutDate) {
		this.memberId = memberId;
		this.bookId = bookId;
		this.checkoutDate = checkoutDate;
	}

	public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

}
