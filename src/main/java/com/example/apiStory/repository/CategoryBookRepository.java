package com.example.apiStory.repository;

import com.example.apiStory.entity.CategoryBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryBookRepository extends JpaRepository<CategoryBook, Long> {
    @Query(value = "SELECT u FROM CategoryBook u WHERE u.bookId in ?1")
    List<CategoryBook> findByBookIds(List<Long> ids);

}