package org.example.service.impl;


import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.dto.admin.UserSearchFilterDto;
import org.example.dto.customer.CustomerLoginDto;
import org.example.dto.customer.CustomerRegisterDto;
import org.example.dto.customer.CustomerUpdateProfileDto;
import org.example.dto.expert.*;
import org.example.entity.Service;
import org.example.entity.User;
import org.example.entity.enumerator.ExpertStatus;
import org.example.entity.enumerator.RoleType;
import org.example.exception.DuplicateResourceException;
import org.example.mapper.ServiceMapper;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.service.OrderService;
import org.example.service.ServiceService;
import org.example.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ServiceService serviceService;
    private final OrderService orderService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ServiceMapper serviceMapper;





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
        Service service = serviceService.findEntityById(serviceId)
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
        Service service = serviceService.findEntityById(serviceId)
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

    @Override
    public void registerCustomer(CustomerRegisterDto dto) {
        if (repository.existsByEmail(dto.email()))
            throw new DuplicateResourceException("Email already in use!");


        User user = userMapper.fromCustomerRegisterDto(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        repository.save(user);
    }

    @Override
    public String saveProfileImage(MultipartFile file, String email) {
        try {
            String filename = "expert_" + email + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String profileDir = "uploads/";
            Path path = Paths.get(profileDir + filename);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile photo", e);
        }
    }


    @Override
    public User loginExpert(ExpertLoginDto dto) {

        User expert = repository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));


        if (!expert.getExpertStatus().equals(ExpertStatus.APPROVED))
            throw new IllegalStateException("Your account is not approved yet.");


        if (!passwordEncoder.matches(dto.password(), expert.getPassword()))
            throw new IllegalArgumentException("Invalid email or password.");

        return expert;
    }

    @Override
    public void loginCustomer(CustomerLoginDto dto) {
        User customer = repository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        if (!customer.getExpertStatus().equals(ExpertStatus.APPROVED))
            throw new IllegalStateException("Your account is not approved yet.");
        if (!passwordEncoder.matches(dto.password(), customer.getPassword()))
            throw new IllegalArgumentException("Invalid email or password.");

    }


    public void updateExpertProfile(Long expertId, ExpertUpdateProfileDto dto) {
        User user = repository.findById(expertId)
                .orElseThrow(() -> new IllegalArgumentException("Expert not found."));

        if (!user.getRole().equals(RoleType.EXPERT))
            throw new IllegalArgumentException("User is not expert.");

        if (orderService.hasActiveOrderForExpert(expertId))
            throw new IllegalStateException("You cannot update your profile while you have active jobs.");

        userMapper.updateExpertProfileFromDto(dto, user);

        if (dto.profilePhoto() != null && !dto.profilePhoto().isEmpty()) {
            Path path = Paths.get("uploads/profile-photos/" + UUID.randomUUID() + "-" + dto.profilePhoto().getOriginalFilename());
            try {
                Files.createDirectories(path.getParent());
                dto.profilePhoto().transferTo(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile photo", e);
            }

            user.setProfilePhoto(path.toString());
        }

        user.setExpertStatus(ExpertStatus.PENDING);
        repository.save(user);
    }

    @Override
    public void updateCustomerProfile(Long customerId, CustomerUpdateProfileDto dto) {
        User user = repository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        if (!user.getRole().equals(RoleType.CUSTOMER))
            throw new IllegalArgumentException("User is not expert.");

        userMapper.updateCustomerProfileFromDto(dto, user);

        repository.save(user);
    }


    public Page<User> searchUsers(UserSearchFilterDto filter) {

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.role() != null) {
                predicates.add(cb.equal(root.get("role"), filter.role()));
            }
            if (filter.name() != null && !filter.name().isBlank()) {
                Predicate firstName = cb.like(cb.lower(root.get("firstName")), "%" + filter.name().toLowerCase() + "%");
                Predicate lastName = cb.like(cb.lower(root.get("lastName")), "%" + filter.name().toLowerCase() + "%");
                predicates.add(cb.or(firstName, lastName));
            }
            if (filter.ratingFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), filter.ratingFrom()));
            }
            if (filter.ratingTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating"), filter.ratingTo()));
            }
            if (filter.service() != null && !filter.service().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.service().toLowerCase() + "%"));
            }
            //join in service not user -> join
            return cb.and(predicates.toArray(new Predicate[0]));
        };


        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null ? filter.size() : 10;

        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(spec, pageable);
    }



    @Override
    public ExpertProfileDto registerExpert(ExpertRegisterDto dto, MultipartFile profilePhoto) {

        String photoUrl;
        try {
            photoUrl = FileStorageService.saveProfilePhoto(profilePhoto);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to save profile photo", e);
        }


        User expert = new User();
        expert.setExpertStatus(ExpertStatus.PENDING);
        expert.setProfilePhoto(photoUrl);

        repository.save(expert);
        return userMapper.mapToProfileDto(expert);
    }
}
