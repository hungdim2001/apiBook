package com.example.apiStory.controller;

import com.example.apiStory.dto.request.ChapterRequest;
import com.example.apiStory.dto.request.RegisterRequest;
import com.example.apiStory.dto.response.ChapterResponse;
import com.example.apiStory.entity.Book;
import com.example.apiStory.entity.Chapter;
import com.example.apiStory.exceptions.NotFoundException;
import com.example.apiStory.helper.ResponseObj;
import com.example.apiStory.repository.BookRepository;
import com.example.apiStory.repository.ChapterRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping("/api/chapters")
@RestController
@CrossOrigin
public class ChapterController {
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    BookRepository bookRepository;

    @Transactional(rollbackOn = Exception.class)
    @ApiOperation(value = "add chapter")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUTHOR')")
    @PostMapping(path = "")
    public ResponseEntity addChapters(@Valid @RequestBody ChapterRequest chapter) {
        Book book = bookRepository.findById(chapter.getBookId()).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "book id not found"));
        Chapter chapterSave = chapterRepository.save(Chapter.builder().bookId(chapter.getBookId())
                .content(chapter.getContent()).number(chapter.getNumber()).build());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "add chapter successfully", ChapterResponse.builder().bookId(book.getId()).name(book.getTitle()).number(chapterSave.getNumber()).content(chapterSave.getContent()).build()));
    }

    @ApiOperation(value = "add chapter")
    @GetMapping("/{bookId}")
    public ResponseEntity getChapter(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "book id not found"));
        List<Chapter> chapter = chapterRepository.getChapterByBookId(bookId);
        List<ChapterResponse> chapterResponses = new ArrayList<>();
        chapter.stream().forEach(item -> {
            ChapterResponse chapterResponse = ChapterResponse.builder().number(item.getNumber()).content(item.getContent())
                    .name(book.getTitle()).bookId(item.getBookId()).build();
            chapterResponses.add(chapterResponse);
        });
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "get chapter successfully", chapterResponses));
    }
}
