package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.emailVerification.EmailVerificationRequestDto;
import org.example.dto.expert.ExpertLoginDto;
import org.example.dto.expert.ExpertProfileDto;
import org.example.dto.expert.ExpertRegisterDto;
import org.example.dto.expert.ExpertUpdateProfileDto;
import org.example.dto.proposal.ProposalCreateByExpertDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.EmailVerificationTokenService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertController {

    private final UserService userService;
    private final ProposalService proposalService;
    private final UserMapper userMapper;
    private final EmailVerificationTokenService emailVerificationTokenService;


//    @PostMapping("/register")
//    public ResponseEntity<String> registerExpert(@ModelAttribute @Valid ExpertRegisterDto dto) {
//        userService.registerExpert(dto);
//        return ResponseEntity.ok("Expert registered successfully. Waiting for approval.");
//    }


    @PostMapping("/register")
    public ResponseEntity<ExpertProfileDto> registerExpert(
            @RequestPart("data") @Valid ExpertRegisterDto data,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto
    ) {
        return ResponseEntity.ok(userService.registerExpert(data, profilePhoto));
    }


    //TODO pass remove from response (entity to dto)
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


    @PostMapping("/send-email")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody @Valid EmailVerificationRequestDto dto) {
        User user = userService.findByEmail(dto.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found with this email"));
        userService.sendEmailVerificationLink(user);
        return ResponseEntity.ok("success send email");
    }


    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok("success verify email");
    }


}