package com.example.apiBook.repository;

import com.example.apiBook.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("select c from Book c where c.status = true ")
    List<Book> findAll();

    @Query(value = "select b.* \n" +
            "from books b,\n" +
            "     categories c,\n" +
            "     category_books cb\n" +
            "where b.id = cb.book_id\n" +
            "  and cb.category_id = c.id\n" +
            "  and c.name = ?1",nativeQuery = true)
    List<Book> findBookByCategory(String category);

    @Query(value = "select count (b)>0 from Book b where b.id =?1 and b.status =true ")
    boolean existsById(Long id);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(concat('%', :title, '%'))")
    List<Book> findBookBySearch(@Param("title") String title);


    @Query("select b from  Book b where  b.isDone = true")
    List<Book> findBookDone();

    Optional<Book> findById(Long id);

    @Query(value = "select b.* from favourite_books fb, books b where fb.user_id =?1 and b.id = fb.book_id", nativeQuery = true)
    List<Book> findByYourBook(Long userId);
}