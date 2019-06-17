package telran.library.dto;

public class ReturnBookData {
// all fields required for picking book
//(readerId, isbn, pickDate (better to use string ISO with parsing on server)
public int readerId;
public String returnDate; //ISO format
public long isbn;
public int getReaderId() {
	return readerId;
}
public String getReturnDate() {
	return returnDate;
}
public long getIsbn() {
	return isbn;
}

}
