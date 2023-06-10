package com.example.apiStory.repository;

import com.example.apiStory.entity.FavouriteBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteBookRepository extends JpaRepository<FavouriteBook, Long> {
    FavouriteBook findByBookIdAndUserId(Long bookId, Long userId);
}
