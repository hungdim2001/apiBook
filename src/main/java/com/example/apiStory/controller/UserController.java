package com.example.apiStory.controller;

import com.example.apiStory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

/* @Autowired
 UserService userService;
    @RequestMapping(path = "/profile", method = PUT, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity updateProfile(@ModelAttribute ProfileRequest profileRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "update profile user successfully", userService.updateProfile(profileRequest, httpServletRequest)));
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity updatePassword(@RequestBody PasswordUpdate passwordUpdate, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "update password successfully", userService.updatePassword(passwordUpdate, httpServletRequest)));
    }*/
}
