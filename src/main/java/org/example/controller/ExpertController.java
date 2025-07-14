package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.expert.ExpertLoginDto;
import org.example.dto.expert.ExpertRegisterDto;
import org.example.dto.expert.ExpertUpdateProfileDto;
import org.example.entity.User;
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



    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid ExpertLoginDto dto) {
        User expert = userService.login(dto);
        return ResponseEntity.ok(expert);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam Long expertId,
            @ModelAttribute @Valid ExpertUpdateProfileDto dto) {
        userService.updateExpertProfile(expertId, dto);
        return ResponseEntity.ok("Profile updated successfully. Waiting for approval.");
    }



}
