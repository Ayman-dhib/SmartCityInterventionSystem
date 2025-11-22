package com.smartcity.intervention;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/hello")
    public String hello() {
        return "🚀 Hello Smart City Project! System is working!";
    }
}