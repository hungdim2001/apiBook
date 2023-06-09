package com.example.apiStory.repository;

import com.example.apiStory.entity.CategoryBook;
import com.example.apiStory.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

   @Query("SELECT c FROM Chapter c WHERE c.bookId = :bookId  ORDER BY c.id")
    List<Chapter> getChapterByBookId(@Param("bookId") Long id);
}
