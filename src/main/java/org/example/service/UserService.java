package org.example.service;

import org.example.dto.admin.UserSearchFilterDto;
import org.example.dto.expert.*;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

//    User save(User entity);
//    List<User> findAll();
//    void deleteById(Long id);
//    boolean existsById(Long id);
//    boolean isManager(User user);

    Optional<User> findById(Long id);

    List<ExpertResponseDto> listPendingExperts();

    void approveExpert(Long expertId);

    void rejectExpert(Long expertId);

    void addExpertToService(Long expertId, Long serviceId);

    void removeExpertFromService(Long expertId, Long serviceId);

    String saveProfileImage(MultipartFile file, String email);

    void registerExpert(ExpertRegisterDto dto);

    User login(ExpertLoginDto dto);

    void updateExpertProfile(Long expertId, ExpertUpdateProfileDto dto);

    Page<User> searchUsers(UserSearchFilterDto filter);

    ExpertProfileDto registerExpert(ExpertRegisterDto dto, MultipartFile profilePhoto);


}