package telran.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.library.dao.AuthorsRepository;
import telran.library.dao.BooksRepository;
import telran.library.dao.ReadersRepository;
import telran.library.dao.RecordsRepository;
import telran.library.dto.AuthorDto;
import telran.library.dto.BookDto;
import telran.library.dto.LibraryReturnCode;
import telran.library.dto.ReaderDto;
import telran.library.entities.Author;
import telran.library.entities.Book;
import telran.library.entities.Reader;
import telran.library.entities.Record;
@Service
@ManagedResource
public class LibraryOrm implements ILibrary {
	@Value("${delayPercent:10}")
	int delayPercent;
	@ManagedAttribute
	public int getDelayPercent() {
		return delayPercent;
	}
	@ManagedAttribute
	public void setDelayPercent(int delayPercent) {
		this.delayPercent = delayPercent;
	}


	@Autowired
	RecordsRepository recordsRepository;
	@Autowired
	BooksRepository booksRepository;
	@Autowired
	ReadersRepository readersRepository;
	@Autowired
	AuthorsRepository authorsRepository;
	@Override
	@Transactional
	public LibraryReturnCode addAuthor(AuthorDto author) {
		if(authorsRepository.existsById(author.name))
		  return LibraryReturnCode.AUTHOR_ALREADY_EXISTS;
		Author authorJpa=new Author(author.name, author.country);
		authorsRepository.save(authorJpa);
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode addBook(BookDto book) {
		if(booksRepository.existsById(book.isbn))
			return LibraryReturnCode.BOOK_ALREADY_EXISTS;
		List<Author> authorsJpa=getAuthorsJpa(book.authorNames);
		if(authorsJpa.size()<book.authorNames.size())
			return LibraryReturnCode.NO_AUTHOR;
		Book bookJpa=new Book(book.isbn, book.title, book.cover, book.amount, book.pickPeriod, authorsJpa);

		booksRepository.save(bookJpa);
		return LibraryReturnCode.OK;
	}

	private List<Author> getAuthorsJpa
	(List<String> authorNames) {
		List<Author> res=new ArrayList<>();
		for(String name:authorNames) {
			if(authorsRepository.existsById(name)) {
			Author authorJpa=authorsRepository.findById(name).get();
			res.add(authorJpa);
			}
			
		}
		return res;
	}

	@Override
	@Transactional
	public LibraryReturnCode pickBook
	(int readerId, long isbn, LocalDate pickDate) {
		Book book=booksRepository.findById(isbn).orElse(null);
		if(book==null)
			return LibraryReturnCode.NO_BOOK;
		int amount=book.getAmount();
		if(amount==0)
			return LibraryReturnCode.ALL_BOOKS_IN_USE;
		Reader reader=readersRepository.findById(readerId).orElse(null);
		if(reader==null)
			return LibraryReturnCode.NO_READER;
		Record recordReader=
		recordsRepository.findByReaderIdAndBookIsbnAndReturnDateNull(readerId,isbn);
		if(recordReader !=null) 
			return LibraryReturnCode.READER_NO_RETURNED_BOOK;
		Record record=new Record(pickDate, book,reader);
		recordsRepository.save(record);
		book.setAmount(amount-1);
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode addReader(ReaderDto reader) {
		if(readersRepository.existsById(reader.id))
			return LibraryReturnCode.READER_ALREADY_EXISTS;
		readersRepository.save(new Reader(reader.id, reader.name,
				reader.year, reader.phone));
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public LibraryReturnCode returnBook(int readerId,
			long isbn, LocalDate returnDate) {
		Record recordReader=
				recordsRepository.
				findByReaderIdAndBookIsbnAndReturnDateNull(readerId,isbn);
		if(recordReader==null)
			return LibraryReturnCode.NO_RECORD_FOR_RETURN;
		Book book=booksRepository.findById(isbn).get();
		book.setAmount(book.getAmount()+1);
		int bookPeriod=book.getPickPeriod();
		int actualPeriod=(int) ChronoUnit.DAYS
		.between(recordReader.getPickDate(), returnDate);
		if(actualPeriod>bookPeriod)
			recordReader.setDelayDays(actualPeriod-bookPeriod);
		recordReader.setReturnDate(returnDate);
		return LibraryReturnCode.OK;
	}

	@Override
	@Transactional
	public List<ReaderDto> getReadersDelayingBooks() {
		LocalDate current=LocalDate.now();
		//Retrieving books that have been picked and not returned
		Stream<Book> booksPicked=recordsRepository
			.findByReturnDateNull()
			.map(Record::getBook).distinct();
		Stream<Reader> readersDelaying=
				booksPicked.flatMap(b->recordsRepository
						.findByBookIsbnAndReturnDateNullAndPickDateBefore
				(b.getIsbn(),
				current.minusDays(b.getPickPeriod()+getAdditionalPeriod(b))))
				.map(Record::getReader).distinct();
		return toListReaderDto(readersDelaying);
	}
	private int getAdditionalPeriod(Book book) {
		return (int) (book.getPickPeriod()*(float)delayPercent/100+0.5);
	}

	private List<ReaderDto> toListReaderDto(Stream<Reader> readers) {
		return readers.map(this::getReaderDto)
				.collect(Collectors.toList());
	}
ReaderDto getReaderDto(Reader reader) {
	return new ReaderDto(reader.getId(),
			reader.getName(), reader.getYear(), reader.getPhoneNumber());
}
	@Override
	@Transactional(readOnly=true)
	public List<AuthorDto> getBookAuthors(long isbn) {
		System.err.println(isbn);
		Book book=booksRepository.findById(isbn).orElse(null);
		List<AuthorDto> res=new ArrayList<>();
		if(book != null)
			book.getAuthors().forEach
			(a->res.add(getAuthorDto(a)));
		return res;
	}

	private AuthorDto getAuthorDto(Author a) {
		System.err.println(a + "author");
		AuthorDto res=new AuthorDto();
		res.country=a.getCountry();
		res.name=a.getName();
		return res;
	}

	@Override
	public List<BookDto> getAuthorBooks(String authorName) {
		List<BookDto> res=new ArrayList<>();
		Author author=authorsRepository.findById(authorName).orElse(null);
		if(author!=null)
			author.getBooks().forEach(b->res.add(getBookDto(b)));
		return res;
	}

	private BookDto getBookDto(Book b) {
		List<String> authorNames=getAuthorNames(b.getAuthors());
		BookDto res=new BookDto
		(b.getIsbn(), b.getTitle(), b.getAmount(),
		authorNames, b.getCover(), b.getPickPeriod());
		return res;
	}

	private List<String> getAuthorNames(List<Author> authors) {
		
		return authors.stream().map(Author::getName)
				.collect(Collectors.toList());
	}

	@Override
	public List<BookDto> getMostPopularBooks(int yearFrom, int yearTo) {
		Long maxCount=recordsRepository.getMaxBookPicks
				(yearFrom, yearTo);
		
		return recordsRepository.findIsbnsMostPopular
				(yearFrom, yearTo, maxCount).stream()
				.map(isbn->booksRepository.getOne(isbn))
				.map(this::getBookDto).collect(Collectors.toList());
	}

	@Override
	public List<ReaderDto> getMostActiveReaders() {
		Long maxCount=recordsRepository.getMaxReaderPicks();
		return recordsRepository.findMostActiveReaderIds(maxCount)
				.stream().map(id->readersRepository.getOne(id))
				.map(this::getReaderDto).collect(Collectors.toList());
	}

	
	@Override
	@Transactional
	public List<BookDto> removeAuthor(String authorName) {
		Author author=authorsRepository.findById(authorName).orElse(null);
		if(author==null)
			return new LinkedList<BookDto>();
		List<Book> booksAuthor=author.getBooks();
		authorsRepository.delete(author);
		return booksAuthor.stream().map(this::getBookDto)
				.collect(Collectors.toList());
	}

}
