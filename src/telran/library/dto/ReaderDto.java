package telran.library.dto;

public class ReaderDto {
	public int id;
	public String name;
	public int year;
	public long phone;
	public ReaderDto() {}
	public ReaderDto(int id, String name, int year, long phone) {
		super();
		this.id = id;
		this.name = name;
		this.year = year;
		this.phone = phone;
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
	public long getPhone() {
		return phone;
	}
	
	
}
