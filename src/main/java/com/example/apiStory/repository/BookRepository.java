package com.example.apiStory.repository;

import com.example.apiStory.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("select c from Book  c where c.status = true ")
    List<Book> findAll();

    @Query(value = "select count (b)>0 from Book b where b.id =?1 and b.status =true ")
    boolean existsById(Long id);

    Optional<Book> findById(Long id);
}