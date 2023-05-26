package com.example.apiStory.dto.request;

import lombok.Data;

@Data
public class Oauth2Request {
    private String email;
    private String firstName;
    private String lastName;
    private String authProvider;
    private String avatarUrl;
}
