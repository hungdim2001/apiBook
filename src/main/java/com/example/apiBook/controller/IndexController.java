package com.example.apiBook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@RequestMapping("")
@Controller
@CrossOrigin
public class IndexController {
    @GetMapping("/index")
    @CrossOrigin
    public String index(HttpSession httpSession) {

        return "indexPage";
    }
}
