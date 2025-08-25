package org.example.service;

import org.example.dto.admin.AdminLoginRequestDto;
import org.example.dto.admin.UserSearchFilterDto;
import org.example.dto.customer.CustomerLoginDto;
import org.example.dto.customer.CustomerRegisterDto;
import org.example.dto.customer.CustomerUpdateProfileDto;
import org.example.dto.expert.*;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User save(User user);

    List<ExpertResponseDto> listPendingExperts();

    void approveExpert(Long expertId);

    void rejectExpert(Long expertId);

    void addExpertToService(Long expertId, Long serviceId);

    void removeExpertFromService(Long expertId, Long serviceId);

    String saveProfileImage(MultipartFile file, String email);

//    void registerExpert(ExpertRegisterDto dto);

    Long registerCustomer(CustomerRegisterDto dto);

    ExpertLoginResponseDto loginExpert(ExpertLoginRequestDto dto);

    void loginCustomer(CustomerLoginDto dto);

    void loginAdmin(AdminLoginRequestDto dto);

    void updateExpertProfile(Long expertId, ExpertUpdateProfileDto dto);

    void updateCustomerProfile(Long customerId, CustomerUpdateProfileDto dto);

    Page<User> searchUsers(UserSearchFilterDto filter);

    ExpertProfileDto registerExpert(ExpertRegisterDto dto, MultipartFile profilePhoto);



}