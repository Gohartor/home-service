package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.expert.ExpertRegisterDto;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/experts")
public class ExpertController {

    private final UserService userService;

    public ExpertController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerExpert(@ModelAttribute @Valid ExpertRegisterDto dto) {
        userService.registerExpert(dto);
        return ResponseEntity.ok("Expert registered successfully. Waiting for approval.");
    }



}
