package org.wellnesshubbackend.wellnesshubbackend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, WellnessHub!";
    }
}
