package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.admin.UserAdminListDto;
import org.example.dto.admin.UserSearchFilterDto;
import org.example.dto.expert.ExpertResponseDto;
import org.example.dto.expert.ExpertServiceAssignRequestDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/pending")
    public ResponseEntity<List<ExpertResponseDto>> listPendingExperts() {
        return ResponseEntity.ok(userService.listPendingExperts());
    }

    @PutMapping("/approve/{expertId}")
    public ResponseEntity<Void> approveExpert(@PathVariable Long expertId) {
        userService.approveExpert(expertId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reject/{expertId}")
    public ResponseEntity<Void> rejectExpert(@PathVariable Long expertId) {
        userService.rejectExpert(expertId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign-service")
    public ResponseEntity<Void> addExpertToService(
            @Valid @RequestBody ExpertServiceAssignRequestDto dto
    ) {
        userService.addExpertToService(dto.expertId(), dto.serviceId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-service")
    public ResponseEntity<Void> removeExpertFromService(
            @Valid @RequestBody ExpertServiceAssignRequestDto dto
    ) {
        userService.removeExpertFromService(dto.expertId(), dto.serviceId());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/search")
    public Page<UserAdminListDto> searchUsers(@RequestBody UserSearchFilterDto filter) {
        Page<User> page = userService.searchUsers(filter);
        return page.map(userMapper::toUserAdminListDto);
    }


}

