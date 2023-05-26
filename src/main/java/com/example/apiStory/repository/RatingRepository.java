package com.example.apiStory.repository;

import com.example.apiStory.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsById(Long id);

    @Query(value = "select count (b)>0 from Rating b where b.userId =?1 and b.bookId =?2 ")
    boolean existsByUserId(Long userId, Long bookId);
}