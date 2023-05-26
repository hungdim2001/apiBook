package com.example.apiStory.dto.request;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;

public class BookRequest {
    private MultipartFile[] avatar;
    private String description;
    private String title;
    private Boolean isDone;
}
