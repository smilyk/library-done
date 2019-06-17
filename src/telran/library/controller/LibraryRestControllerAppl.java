package telran.library.controller;

import java.time.LocalDate;
import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import telran.library.dto.*;
import telran.library.service.ILibrary;
@RestController
public class LibraryRestControllerAppl {
@Autowired
ILibrary library;
@PreDestroy
public void displayRedearsDelayers() {
	System.out.println("List of delayers:");
	library.getReadersDelayingBooks().forEach(System.out::println);
}
@PostMapping(value=LibraryApiConstants.ADD_BOOK)
LibraryReturnCode addBook(@RequestBody BookDto book) {
	return library.addBook(book);
}
@PostMapping(value=LibraryApiConstants.ADD_AUTHOR)
LibraryReturnCode addAuthor(@RequestBody AuthorDto author) {
	return library.addAuthor(author);
}
@PostMapping(value=LibraryApiConstants.PICK_BOOK)
LibraryReturnCode pickBook(@RequestBody PickBookData pbd) {
	return library.pickBook(pbd.readerId, pbd.isbn,
			LocalDate.parse(pbd.pickDate));
}
@PostMapping(value=LibraryApiConstants.RETURN_BOOK)
LibraryReturnCode returnBook (@RequestBody ReturnBookData rbd)
{
	try {
		LocalDate returnDate=LocalDate.parse(rbd.returnDate);
		return library.returnBook(rbd.readerId, rbd.isbn, returnDate);
	} catch (Exception e) {
		return LibraryReturnCode.WRONG_DATE_FORMAT;
	}
}

@PostMapping(value=LibraryApiConstants.ADD_READER)
LibraryReturnCode addReader(@RequestBody ReaderDto reader) {
	return library.addReader(reader);
}
@DeleteMapping(value=LibraryApiConstants.REMOVE_AUTHOR)
List<BookDto> removeAuthor(@PathVariable String authorName){
	return library.removeAuthor(authorName);
}
@GetMapping(value=LibraryApiConstants.GET_READERS_DELAYING)
List<ReaderDto> readersDelaying(){
	return library.getReadersDelayingBooks();
}
@GetMapping(value=LibraryApiConstants.GET_BOOK_AUTHORS)
List<AuthorDto> getBookAuthors(@PathVariable(name=
LibraryApiConstants.ISBN) long isbn){
	return library.getBookAuthors(isbn);
}

@GetMapping(value = LibraryApiConstants.ACTIVE)
	List<ReaderDto> getActiveReaders(){
	return library.getMostActiveReaders();
}


@GetMapping(value=LibraryApiConstants.GET_AUTHOR_BOOKS)
List<BookDto> getAuthorBooks(@PathVariable(name=
LibraryApiConstants.AUTHOR_NAME) String authorName){
	return library.getAuthorBooks(authorName);
}

	

}
