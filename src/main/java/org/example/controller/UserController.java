package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.admin.UserAdminListDto;
import org.example.dto.admin.UserSearchFilterDto;
import org.example.dto.expert.ExpertProfileDto;
import org.example.dto.expert.ExpertRegisterDto;
import org.example.dto.expert.ExpertResponseDto;
import org.example.dto.expert.ExpertServiceAssignRequestDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/pending")
    public ResponseEntity<List<ExpertResponseDto>> listPendingExperts() {
        return ResponseEntity.ok(userService.listPendingExperts());
    }

    @PutMapping("/approve/{expertId}")
    public ResponseEntity<String> approveExpert(@PathVariable Long expertId) {
        userService.approveExpert(expertId);
        return ResponseEntity.ok("approved");
    }

    @PutMapping("/reject/{expertId}")
    public ResponseEntity<String> rejectExpert(@PathVariable Long expertId) {
        userService.rejectExpert(expertId);
        return ResponseEntity.ok("rejected");
    }

    @PostMapping("/assign-service")
    public ResponseEntity<String> addExpertToService(
            @Valid @RequestBody ExpertServiceAssignRequestDto dto
    ) {
        userService.addExpertToService(dto.expertId(), dto.serviceId());
        return ResponseEntity.ok("added");
    }

    @PostMapping("/remove-service")
    public ResponseEntity<String> removeExpertFromService(
            @Valid @RequestBody ExpertServiceAssignRequestDto dto
    ) {
        userService.removeExpertFromService(dto.expertId(), dto.serviceId());
        return ResponseEntity.ok("removed");
    }


    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserAdminListDto> searchUsers(@RequestBody UserSearchFilterDto filter) {
        Page<User> page = userService.searchUsers(filter);
        return page.map(userMapper::toUserAdminListDto);
    }


    @PostMapping("/register")
    public ResponseEntity<ExpertProfileDto> registerExpert(
            @RequestPart("data") @Valid ExpertRegisterDto data,
            @RequestPart("profilePhoto") @Valid MultipartFile profilePhoto
    ) {
        ExpertProfileDto profileDto = userService.registerExpert(data, profilePhoto);
        return ResponseEntity.ok(profileDto);
    }
}

