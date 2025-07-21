package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.auth.UserPrincipal;
import org.example.dto.expert.ExpertLoginDto;
import org.example.dto.expert.ExpertRegisterDto;
import org.example.dto.expert.ExpertUpdateProfileDto;
import org.example.dto.proposal.ProposalCreateByExpertDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertController {

    private final UserService userService;
    private final ProposalService proposalService;
    private final UserMapper userMapper;


    @PostMapping("/register")
    public ResponseEntity<String> registerExpert(@ModelAttribute @Valid ExpertRegisterDto dto) {
        userService.registerExpert(dto);
        return ResponseEntity.ok("Expert registered successfully. Waiting for approval.");
    }


    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid ExpertLoginDto dto) {
        User expert = userService.loginExpert(dto);
        return ResponseEntity.ok(expert);
    }


    @PutMapping("/update-profile")
    public ResponseEntity<String> updateProfile(
            @RequestParam Long expertId,
            @ModelAttribute @Valid ExpertUpdateProfileDto dto) {
        userService.updateExpertProfile(expertId, dto);
        return ResponseEntity.ok("Profile updated successfully. Waiting for approval.");
    }


    @PostMapping("/submit-proposal")
    public ResponseEntity<String> submitProposal(
            @RequestParam Long expertId,
            @RequestBody @Valid ProposalCreateByExpertDto dto) {
        proposalService.submitProposalByExpert(expertId, dto);
        return ResponseEntity.ok("success submit proposal for expert.");
    }


}