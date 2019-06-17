package telran.library.entities;
import java.util.List;

import javax.persistence.*;
@Table(name="authors")
@Entity
public class Author {
@Id
String name;
String country;
@ManyToMany(mappedBy="authors",cascade=CascadeType.REMOVE)
List<Book> books;
public Author() {
}
public Author(String name, String country) {
	super();
	this.name = name;
	this.country = country;
}
public String getName() {
	return name;
}
public String getCountry() {
	return country;
}
public List<Book> getBooks() {
	return books;
}

}
