package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
public class TestController {
    @GetMapping("/")
    public String home() {
        return "CI/CD 배포 성공!";
    }
}