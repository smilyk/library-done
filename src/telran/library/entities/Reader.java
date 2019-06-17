package telran.library.entities;
import java.util.List;

import javax.persistence.*;
@Table(name="readers")
@Entity
public class Reader {
@Id
int id;
String name;
int year;//birthyear
long phoneNumber;
@OneToMany(mappedBy="reader")
List<Record> records;
public Reader() {
}
public Reader(int id, String name, int year, long phoneNumber) {
	super();
	this.id = id;
	this.name = name;
	this.year = year;
	this.phoneNumber = phoneNumber;
}
public long getPhoneNumber() {
	return phoneNumber;
}
public void setPhoneNumber(long phoneNumber) {
	this.phoneNumber = phoneNumber;
}
public int getId() {
	return id;
}
public String getName() {
	return name;
}
public int getYear() {
	return year;
}
public List<Record> getRecords() {
	return records;
}

}
