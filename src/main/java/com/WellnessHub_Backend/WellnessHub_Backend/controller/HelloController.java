package com.WellnessHub_Backend.WellnessHub_Backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, WellnessHub!";
    }
}
