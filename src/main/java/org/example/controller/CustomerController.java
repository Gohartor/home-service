package org.example.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.customer.CustomerRegisterDto;
import org.example.dto.expert.ExpertRegisterDto;
import org.example.dto.service.ServiceRequestDto;
import org.example.dto.service.ServiceResponseDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.service.ServiceService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final ServiceService serviceService;
    private final UserMapper userMapper;


    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@RequestBody @Valid CustomerRegisterDto dto) {
        userService.registerCustomer(dto);
        return ResponseEntity.ok("Customer registered successfully.");
    }


}
