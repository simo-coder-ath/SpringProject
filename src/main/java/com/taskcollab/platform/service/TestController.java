package com.taskcollab.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping
    public String test() {
        return " Api is working!";
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "hello from task";
    }
    @GetMapping("/protected")
public String protectedEndpoint() {
    return "protected endpoint!";
}
}