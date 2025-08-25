package org.example.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.customer.CustomerLoginDto;
import org.example.dto.customer.CustomerRegisterDto;
import org.example.dto.customer.CustomerRegisterResponseDto;
import org.example.dto.customer.CustomerUpdateProfileDto;
import org.example.dto.expert.ExpertUpdateProfileDto;
import org.example.dto.order.CreateOrderByCustomerDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.OrderService;
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
    private final OrderService orderService;
    private final UserMapper userMapper;


    @PostMapping("/register")
    public ResponseEntity<CustomerRegisterResponseDto> registerCustomer(@RequestBody @Valid CustomerRegisterDto dto) {
        Long l = userService.registerCustomer(dto);
        CustomerRegisterResponseDto customerRegisterResponseDto = new CustomerRegisterResponseDto(l, dto.firstName(), dto.lastName(), dto.email());
        return ResponseEntity.ok(customerRegisterResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid CustomerLoginDto dto) {
        userService.loginCustomer(dto);
        return ResponseEntity.ok("success customer login");
    }


    @PutMapping("/update-profile")
    public ResponseEntity<String>updateProfile(
            @RequestParam Long customerId,
            @RequestBody @Valid CustomerUpdateProfileDto dto) {
        userService.updateCustomerProfile(customerId, dto);
        return ResponseEntity.ok("Profile updated successfully.");
    }




}
