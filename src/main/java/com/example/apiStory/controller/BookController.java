package com.example.apiStory.controller;

import com.example.apiStory.dto.response.BookResponse;
import com.example.apiStory.entity.Book;
import com.example.apiStory.entity.Category;
import com.example.apiStory.entity.CategoryBook;
import com.example.apiStory.exceptions.NotFoundException;
import com.example.apiStory.helper.ResponseObj;
import com.example.apiStory.repository.BookRepository;
import com.example.apiStory.repository.CategoryBookRepository;
import com.example.apiStory.repository.CategoryRepository;
import com.example.apiStory.service.FtpService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping("/api/books")
@RestController
@CrossOrigin
public class BookController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FtpService ftpService;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryBookRepository categoryBookRepository;

    @Transactional(rollbackOn = Exception.class)
    @ApiOperation(value = "add book")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AUTHOR')")
    @RequestMapping(path = "", method = POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @CrossOrigin
    public ResponseEntity addBook(@RequestParam("title")
                                  String title,
                                  @RequestParam("categoryName")
                                  String categoryName,
                                  @RequestParam("description")
                                  String description,
                                  @RequestParam("image")
                                  MultipartFile image,
                                  @RequestParam("isDone")
                                  Boolean isDone) throws IOException {
        Category category = categoryRepository.findByName(categoryName).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "category  not exist")
        );
        String fileName = System.currentTimeMillis() + image.getOriginalFilename();
        ftpService.uploadFile(image, fileName);
        Book bookSave = bookRepository.save(Book.builder().avatarUrl(fileName)
                .isDone(isDone).description(description).status(true).title(title).build());
        CategoryBook categoryBookSave = categoryBookRepository.save(CategoryBook.builder().bookId(bookSave.getId()).categoryId(category.getId()).build());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "save user successfully", bookSave));
    }

    @ApiOperation(value = "get book")
    @GetMapping("")
    @CrossOrigin
    public ResponseEntity getBook(HttpServletRequest request) throws IOException {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
//      Lấy sách tất cả truyện
        List<Book> books = bookRepository.findAll();
        List<Long> bookIds = books.stream().map(item -> item.getId()).collect(Collectors.toList());
//      Tạo một List CategoryBooks lấy từ database tìm theo bookIds
        List<CategoryBook> categoryBooks = categoryBookRepository.findByBookIds(bookIds);
//      Tạo ra một List categoryIds dựa trên categoryBooks.id
        List<Long> categoryIds = categoryBooks.stream().map(item -> item.getCategoryId()).collect(Collectors.toList());
//      Tạo ra môt List Categories theo như CategoryId trả về
        List<Category> categories = categoryRepository.findBydIs(categoryIds);
        Map<Long, Category> categoryMap = new HashMap<>();
        Map<Long, CategoryBook> categoryBookMap = new HashMap<>();
        categories.stream().forEach(
                item -> {categoryMap.put(item.getId(), item);
        });
        categoryBooks.stream().forEach(item ->
        {categoryBookMap.put(item.getBookId(), item);
        });
        List<BookResponse> bookResponses = new ArrayList<>();
        books.stream().forEach((item) -> {
            BookResponse bookResponse = new BookResponse();
            bookResponse.setId(item.getId());
            bookResponse.setDescription(item.getDescription());
            bookResponse.setStatus(item.getStatus());
            bookResponse.setTitle(item.getTitle());
            bookResponse.setAvatarUrl(baseUrl + "/api/books/thumbnail/" + item.getAvatarUrl());
            bookResponse.setIsDone(item.getIsDone());
            String name = categoryMap.get(categoryBookMap.get(item.getId()).getCategoryId()).getName();
            bookResponse.setCategory(name);
            bookResponses.add(bookResponse);
        });
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "get books successfully", bookResponses));
    }

    @ApiOperation(value = "get book")
    @GetMapping("/search/{title}")
    @CrossOrigin
    public ResponseEntity getBookBySearch(HttpServletRequest request,@PathVariable String title) throws IOException {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        List<Book> books = bookRepository.findBookBySearch(title);
        List<Long> bookIds = books.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<CategoryBook> categoryBooks = categoryBookRepository.findByBookIds(bookIds);
        List<Long> categoryIds = categoryBooks.stream().map(item -> item.getCategoryId()).collect(Collectors.toList());
        List<Category> categories = categoryRepository.findBydIs(categoryIds);
        Map<Long, Category> categoryMap = new HashMap<>();
        Map<Long, CategoryBook> categoryBookMap = new HashMap<>();
        categories.stream().forEach(
                item -> {categoryMap.put(item.getId(), item);
                });
        categoryBooks.stream().forEach(item ->
        {categoryBookMap.put(item.getBookId(), item);
        });
        List<BookResponse> bookResponses = new ArrayList<>();
        books.stream().forEach((item) -> {
            BookResponse bookResponse = new BookResponse();
            bookResponse.setId(item.getId());
            bookResponse.setDescription(item.getDescription());
            bookResponse.setStatus(item.getStatus());
            bookResponse.setTitle(item.getTitle());
            bookResponse.setAvatarUrl(baseUrl + "/api/books/thumbnail/" + item.getAvatarUrl());
            bookResponse.setIsDone(item.getIsDone());
            String name = categoryMap.get(categoryBookMap.get(item.getId()).getCategoryId()).getName();
            bookResponse.setCategory(name);
            bookResponses.add(bookResponse);
        });
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "get books successfully", bookResponses));
    }



    @ApiOperation(value = "get book")
    @GetMapping("/done")
    @CrossOrigin
    public ResponseEntity getBookDone(HttpServletRequest request) throws IOException {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        List<Book> books = bookRepository.findBookDone();
        List<Long> bookIds = books.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<CategoryBook> categoryBooks = categoryBookRepository.findByBookIds(bookIds);
        List<Long> categoryIds = categoryBooks.stream().map(item -> item.getCategoryId()).collect(Collectors.toList());
        List<Category> categories = categoryRepository.findBydIs(categoryIds);
        Map<Long, Category> categoryMap = new HashMap<>();
        Map<Long, CategoryBook> categoryBookMap = new HashMap<>();
        categories.stream().forEach(
                item -> {categoryMap.put(item.getId(), item);
                });
        categoryBooks.stream().forEach(item ->
        {categoryBookMap.put(item.getBookId(), item);
        });
        List<BookResponse> bookResponses = new ArrayList<>();
        books.stream().forEach((item) -> {
            BookResponse bookResponse = new BookResponse();
            bookResponse.setId(item.getId());
            bookResponse.setDescription(item.getDescription());
            bookResponse.setStatus(item.getStatus());
            bookResponse.setTitle(item.getTitle());
            bookResponse.setAvatarUrl(baseUrl + "/api/books/thumbnail/" + item.getAvatarUrl());
            bookResponse.setIsDone(item.getIsDone());
            String name = categoryMap.get(categoryBookMap.get(item.getId()).getCategoryId()).getName();
            bookResponse.setCategory(name);
            bookResponses.add(bookResponse);
        });
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "get books successfully", bookResponses));
    }

    @GetMapping("/thumbnail/{fileName}")
    @CrossOrigin
    public ResponseEntity<byte[]> handleFileUpload(@PathVariable String fileName) throws IOException {
        byte[] fileContent = ftpService.retrieveFile(fileName);
        String contentType = URLConnection.guessContentTypeFromName(fileName);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

    }


    @ApiOperation(value = "get book category")
    @GetMapping("/{category}")
    @CrossOrigin
    public ResponseEntity getBookByCategory(HttpServletRequest request, @PathVariable String category) throws IOException {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        List<Book> books = bookRepository.findBookByCategory(category);
        List<Long> bookIds = books.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<CategoryBook> categoryBooks = categoryBookRepository.findByBookIds(bookIds);
        List<Long> categoryIds = categoryBooks.stream().map(item -> item.getCategoryId()).collect(Collectors.toList());
        List<Category> categories = categoryRepository.findBydIs(categoryIds);
        Map<Long, Category> categoryMap = new HashMap<>();
        Map<Long, CategoryBook> categoryBookMap = new HashMap<>();
        categories.stream().forEach(
                item -> {categoryMap.put(item.getId(), item);
                });
        categoryBooks.stream().forEach(item ->
        {categoryBookMap.put(item.getBookId(), item);
        });
        List<BookResponse> bookResponses = new ArrayList<>();
        books.stream().forEach((item) -> {
            BookResponse bookResponse = new BookResponse();
            bookResponse.setId(item.getId());
            bookResponse.setDescription(item.getDescription());
            bookResponse.setStatus(item.getStatus());
            bookResponse.setTitle(item.getTitle());
            bookResponse.setAvatarUrl(baseUrl + "/api/books/thumbnail/" + item.getAvatarUrl());
            bookResponse.setIsDone(item.getIsDone());
            String name = categoryMap.get(categoryBookMap.get(item.getId()).getCategoryId()).getName();
            bookResponse.setCategory(name);
            bookResponses.add(bookResponse);
        });
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "get books successfully", bookResponses));
    }



}
