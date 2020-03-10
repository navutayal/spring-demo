package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Greeting {

    @GetMapping("/")
    public String getHello() {
        return "My Name is Naveen Gupta";
    }

}