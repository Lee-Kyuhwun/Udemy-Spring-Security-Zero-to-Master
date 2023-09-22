package com.easybytes.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoticeController {


    @GetMapping("/notice")
    public String getNotice() {
        return "Here are the notice details from the DB";
    }

}
