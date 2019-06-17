package telran.library.dto;

public interface LibraryApiConstants {
String ADD_BOOK="/book/add";
String ADD_AUTHOR="/author/add";
String PICK_BOOK = "/book/pick";
String ADD_READER="/reader/add";
String RETURN_BOOK = "/book/return";
String GET_READERS_DELAYING = "/readers/delaying/get";
String GET_BOOK_AUTHORS = "/authors/book/get/{isbn}";
String GET_AUTHOR_BOOKS ="/books/author/get/{author}";
String ISBN="isbn";
String AUTHOR_NAME="author";
String REMOVE_AUTHOR = "/author/remove/{authorName}";
String ACTIVE = "/active";
}
