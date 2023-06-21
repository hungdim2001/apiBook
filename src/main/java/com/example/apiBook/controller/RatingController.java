package com.example.apiBook.controller;

import com.example.apiBook.dto.request.RatingRequest;
import com.example.apiBook.entity.Rating;
import com.example.apiBook.exceptions.NotFoundException;
import com.example.apiBook.helper.ResponseObj;
import com.example.apiBook.repository.BookRepository;
import com.example.apiBook.repository.RatingRepository;
import com.example.apiBook.util.UserUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUTHOR', 'ROLE_USER')")
    public ResponseEntity postRating(@Valid @RequestBody RatingRequest ratingRequest) {
        if (!bookRepository.existsById(ratingRequest.getBookId())) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "book  not exist");
        }
        Long userId = UserUtil.getUserId();
        Optional<Rating> rating = ratingRepository.findByUserId(userId, ratingRequest.getBookId());
        if (rating.isPresent()) {
            rating.get().setStar(ratingRequest.getStar());
            ratingRepository.save(rating.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "get books successfully", ratingRepository.save(rating.get())));


        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "get books successfully", ratingRepository.save(Rating.builder().star(ratingRequest.getStar()).bookId(ratingRequest.getBookId()).userId(userId).build())));

    }
}
