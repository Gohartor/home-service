package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.service.ServiceRequestDto;
import org.example.dto.service.ServiceResponseDto;
import org.example.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<ServiceResponseDto> createService(
            @Valid @RequestBody ServiceRequestDto dto
    ) {
        ServiceResponseDto response = serviceService.createService(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDto dto
    ) {
        ServiceResponseDto response = serviceService.updateService(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> listServices(
            @RequestParam(required = false) Long parentId
    ) {
        List<ServiceResponseDto> result = serviceService.listServices(parentId);
        return ResponseEntity.ok(result);
    }
}
