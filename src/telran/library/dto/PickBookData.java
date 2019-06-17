package telran.library.dto;

public class PickBookData {
// all fields required for picking book
//(readerId, isbn, pickDate (better to use string ISO with parsing on server)
public int readerId;
public String pickDate; //ISO format
public long isbn;
public int getReaderId() {
	return readerId;
}
public String getPickDate() {
	return pickDate;
}
public long getIsbn() {
	return isbn;
}

}
