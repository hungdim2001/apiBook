package com.example.apiStory.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterRequest {
    private String password;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
}
