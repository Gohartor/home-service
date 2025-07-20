package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.service.ServiceRequestDto;
import org.example.dto.service.ServiceResponseDto;
import org.example.entity.Service;
import org.example.mapper.UserMapper;
import org.example.mapper.UserMapperImpl;
import org.example.service.ServiceService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ServiceService serviceService;


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
    public ResponseEntity<String> approveExpert(@PathVariable Long id) {
        userService.approveExpert(id);
        return ResponseEntity.ok("Success approve expert");
    }

    @PutMapping("/add-expert-to-service")
    public ResponseEntity<?> addExpertToService(@RequestParam Long expertId, @RequestParam Long serviceId) {
        userService.addExpertToService(expertId, serviceId);
        return ResponseEntity.ok("Success add expert to service");
    }


    @PutMapping("/remove-expert-from-service")
    public ResponseEntity<?> removeExpertFromService(@RequestParam Long expertId, @RequestParam Long serviceId) {
        userService.removeExpertFromService(expertId, serviceId);
        return ResponseEntity.ok("Success remove expert from service");
    }
}
