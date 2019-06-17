package telran.library.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.library.entities.*;
public interface RecordsRepository extends
JpaRepository<Record, Integer> {

	Stream<Record> findByReturnDateNull();
	Record findByReaderIdAndBookIsbnAndReturnDateNull(int readerId, long isbn);
	Stream<Record>
	findByBookIsbnAndReturnDateNullAndPickDateBefore
	(long isbn,LocalDate pickDelayed);
	@Query(value="select count(*) from records  join readers on readers.id=reader_id "
			+ "where year between :from and :to "
			+ "group by book_isbn order by count(*) desc limit 1",nativeQuery=true)
	Long getMaxBookPicks(@Param("from") int yearFrom,
			@Param("to") int yearTo );
	@Query("select book.isbn from Record where reader.year "
			+ "between :from and :to group by book.isbn "
			+ "having count(*)=:count")
	List<Long> findIsbnsMostPopular(@Param("from") int yearFrom,
			@Param("to") int yearTo, @Param ("count")long maxCount);
	@Query(value="select count(*) from records "
			+ " group by reader_id order by "
			+ "count(*) desc limit 1",nativeQuery=true)
	Long getMaxReaderPicks();
	@Query("select reader.id from Record group by reader.id having"
			+ " count(*)=:count")
	List<Integer> findMostActiveReaderIds(@Param("count") long maxCount);
}
