package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.expert.ExpertResponseDto;
import org.example.dto.expert.ExpertServiceAssignRequestDto;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/experts")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
}
