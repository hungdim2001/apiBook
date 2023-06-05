package com.example.apiStory.controller;

import com.example.apiStory.dto.request.CategoryRequest;
import com.example.apiStory.dto.request.RatingRequest;
import com.example.apiStory.entity.Category;
import com.example.apiStory.entity.Rating;
import com.example.apiStory.exceptions.DuplicateException;
import com.example.apiStory.exceptions.NotFoundException;
import com.example.apiStory.helper.ResponseObj;
import com.example.apiStory.repository.BookRepository;
import com.example.apiStory.repository.RatingRepository;
import com.example.apiStory.util.UserUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/rating")
@RestController
public class RatingController {
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    BookRepository bookRepository;

    @PostMapping("")
    @ApiOperation(value = "add rating")
    @CrossOrigin
    public ResponseEntity postRating(@Valid @RequestBody RatingRequest ratingRequest) {
        if (!bookRepository.existsById(ratingRequest.getBookId())) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "book  not exist");
        }
        Long userId = UserUtil.getUserId();
        if (ratingRepository.existsByUserId(userId, ratingRequest.getBookId())) {
            throw new DuplicateException(HttpStatus.BAD_REQUEST, "duplicate");

        }
        ratingRepository.save(Rating.builder().star(ratingRequest.getStar()).bookId(ratingRequest.getBookId()).userId(userId).build());
        throw new DuplicateException(HttpStatus.CONFLICT, "duplicate record");
    }
}
