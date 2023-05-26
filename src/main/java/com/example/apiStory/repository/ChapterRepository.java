package com.example.apiStory.repository;

import com.example.apiStory.entity.CategoryBook;
import com.example.apiStory.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> getChapterByBookId(Long id);
}
