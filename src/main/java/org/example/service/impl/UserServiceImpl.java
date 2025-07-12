package org.example.service.impl;

import org.example.dto.expert.ExpertRegisterDto;
import org.example.dto.expert.ExpertResponseDto;
import org.example.entity.User;
import org.example.entity.Service;
import org.example.entity.enumerator.ExpertStatus;
import org.example.entity.enumerator.RoleType;
import org.example.exception.DuplicateResourceException;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.service.ServiceService;
import org.example.service.UserService;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ServiceService serviceService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    private final String profileDir = "uploads/";


    public UserServiceImpl(
            UserRepository repository,
            ServiceService serviceService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.serviceService = serviceService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ExpertResponseDto> listPendingExperts() {
        return repository.findByRoleAndExpertStatus(RoleType.EXPERT, ExpertStatus.NEW)
                .stream()
                .map(userMapper::toExpertResponseDto)
                .toList();
    }

    @Override
    public void approveExpert(Long expertId) {
        User user = repository.findByIdAndRole(expertId, RoleType.EXPERT)
                .orElseThrow(() -> new NotFoundException("Expert not found"));
        user.setExpertStatus(ExpertStatus.APPROVED);
        repository.save(user);
    }

    @Override
    public void rejectExpert(Long expertId) {
        User expert = repository.findByIdAndRole(expertId, RoleType.EXPERT)
                .orElseThrow(() -> new NotFoundException("Expert not found"));
        expert.setExpertStatus(ExpertStatus.REJECTED);
        repository.save(expert);
    }

    @Override
    public void addExpertToService(Long expertId, Long serviceId) {
        User expert = repository.findByIdAndRole(expertId, RoleType.EXPERT)
                .orElseThrow(() -> new NotFoundException("Expert not found"));
        Service service = serviceService.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        expert.getServices().add(service);
        service.getExperts().add(expert);

        repository.save(expert);
        serviceService.save(service);
    }

    @Override
    public void removeExpertFromService(Long expertId, Long serviceId) {
        User expert = repository.findByIdAndRole(expertId, RoleType.EXPERT)
                .orElseThrow(() -> new NotFoundException("Expert not found"));
        Service service = serviceService.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));
        expert.getServices().remove(service);
        service.getExperts().remove(expert);
        repository.save(expert);
        serviceService.save(service);
    }



    @Override
    @Transactional
    public void registerExpert(ExpertRegisterDto dto) {
        if (repository.existsByEmail(dto.email()))
            throw new DuplicateResourceException("Email already in use!");

        MultipartFile photo = dto.profilePhoto();
        if (photo == null || photo.isEmpty())
            throw new IllegalArgumentException("Profile photo is required!");
        if (photo.getSize() > 300 * 1024)
            throw new IllegalArgumentException("File too large!");

        User user = userMapper.fromExpertRegisterDto(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setProfilePhoto(saveProfileImage(photo, dto.email()));

        repository.save(user);
    }

    private String saveProfileImage(MultipartFile file, String email) {
        try {
            String filename = "expert_" + email + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(profileDir + filename);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile photo", e);
        }
    }
}
