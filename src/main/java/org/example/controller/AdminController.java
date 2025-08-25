package org.example.controller;

import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ApiResponse;
import org.example.dto.admin.AdminLoginRequestDto;
import org.example.dto.service.ServiceRequestDto;
import org.example.dto.service.ServiceResponseDto;
import org.example.entity.Service;
import org.example.mapper.UserMapper;
import org.example.mapper.UserMapperImpl;
import org.example.service.ServiceService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ServiceService serviceService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AdminLoginRequestDto dto) {
        userService.loginAdmin(dto);
        return ResponseEntity.ok(new  ApiResponse("success login admin"));
    }


    @PostMapping("/create-service")
    public ResponseEntity<ServiceResponseDto> createService(@RequestBody ServiceRequestDto dto) {
        return ResponseEntity.ok(serviceService.createService(dto));
    }

    @GetMapping("/get-services-list")
    public ResponseEntity<List<ServiceResponseDto>> listServices(@RequestParam Long parentId) {
        return ResponseEntity.ok(serviceService.listServices(parentId));
    }

    @GetMapping("/get-service/{id}")
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.findById(id).orElseThrow());
    }

    @PutMapping("/update-service/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(@PathVariable Long id,
                                                            @RequestBody ServiceRequestDto dto) {
        return ResponseEntity.ok(serviceService.updateService(id, dto));
    }

    @DeleteMapping("/delete-service/{id}")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok("success delete service");
    }


    @PutMapping("/approve-expert/{id}")
    public ResponseEntity<ApiResponse> approveExpert(@PathVariable Long id) {
        userService.approveExpert(id);
        return ResponseEntity.ok(new  ApiResponse("success approve expert"));
    }

    @PutMapping("/add-expert-to-service")
    public ResponseEntity<ApiResponse> addExpertToService(@RequestParam Long expertId, @RequestParam Long serviceId) {
        userService.addExpertToService(expertId, serviceId);
        return ResponseEntity.ok(new ApiResponse("success add expert to service"));
    }


    @PutMapping("/remove-expert-from-service")
    public ResponseEntity<?> removeExpertFromService(@RequestParam Long expertId, @RequestParam Long serviceId) {
        userService.removeExpertFromService(expertId, serviceId);
        return ResponseEntity.ok("Success remove expert from service");
    }
}