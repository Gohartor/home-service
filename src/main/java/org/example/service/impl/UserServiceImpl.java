package org.example.service.impl;

import org.example.dto.expert.ExpertResponseDto;
import org.example.entity.User;
import org.example.entity.Service;
import org.example.entity.enumerator.ExpertStatus;
import org.example.entity.enumerator.RoleType;
import org.example.mapper.UserMapper;
import org.example.repository.ServiceRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;

import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ServiceRepository serviceRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository repository,
            ServiceRepository serviceRepository,
            UserMapper userMapper) {
        this.repository = repository;
        this.serviceRepository = serviceRepository;
        this.userMapper = userMapper;
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
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        expert.getServices().add(service);
        service.getExperts().add(expert);

        repository.save(expert);
        serviceRepository.save(service);
    }

    @Override
    public void removeExpertFromService(Long expertId, Long serviceId) {
        User expert = repository.findByIdAndRole(expertId, RoleType.EXPERT)
                .orElseThrow(() -> new NotFoundException("Expert not found"));
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));
        expert.getServices().remove(service);
        service.getExperts().remove(expert);
        repository.save(expert);
        serviceRepository.save(service);
    }
}
