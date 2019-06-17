package telran.library.dto;

public class AuthorDto {
public String name;
public String country;
public String getName() {
	return name;
}
public String getCountry() {
	return country;
}
@Override
public String toString() {
	return "AuthorDto [name=" + name + ", country=" + country + "]";
}

}
