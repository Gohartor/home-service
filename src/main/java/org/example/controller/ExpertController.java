package org.example.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cef.callback.CefContextMenuParams;
import org.example.dto.ApiResponse;
import org.example.dto.emailVerification.EmailVerificationRequestDto;
import org.example.dto.expert.*;
import org.example.dto.proposal.ProposalCreateByExpertDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.security.CustomUserDetails;
import org.example.service.EmailVerificationTokenService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertController {

    private final UserService userService;
    private final ProposalService proposalService;
    private final EmailVerificationTokenService emailVerificationTokenService;


//    @PostMapping("/register")
//    public ResponseEntity<String> registerExpert(@ModelAttribute @Valid ExpertRegisterDto dto) {
//        userService.registerExpert(dto);
//        return ResponseEntity.ok("Expert registered successfully. Waiting for approval.");
//    }


    @PostMapping(value="/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExpertProfileDto> registerExpert(
            @RequestPart("data") @Valid ExpertRegisterDto data,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto
    ) {
        return ResponseEntity.ok(userService.registerExpert(data, profilePhoto));
    }


    //TODO pass remove from response (entity to dto) -----> DONE
    @PostMapping("/login")
    public ResponseEntity<ExpertLoginResponseDto> login(@RequestBody @Valid ExpertLoginRequestDto dto) {
        return ResponseEntity.ok(userService.loginExpert(dto));
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
    public ResponseEntity<ApiResponse> sendVerificationEmail(@RequestBody VerifyMailRequestDto dto) {
        User user = userService.findByEmail(dto.mail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found with this email"));
        emailVerificationTokenService.sendEmailVerificationLink(user);
        return ResponseEntity.ok(new ApiResponse("Email verification link has been sent"));
    }


    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token) {
        emailVerificationTokenService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse("success verify email"));
    }


}