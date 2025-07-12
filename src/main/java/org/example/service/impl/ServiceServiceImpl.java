package org.example.service.impl;

import org.example.dto.service.ServiceRequestDto;
import org.example.dto.service.ServiceResponseDto;
import org.example.entity.Service;
import org.example.entity.User;
import org.example.exception.DuplicateException;
import org.example.mapper.ServiceMapper;
import org.example.repository.ServiceRepository;
import org.example.service.ServiceService;
import org.example.service.UserService;

import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository repository;
    private final ServiceMapper serviceMapper;

    public ServiceServiceImpl(ServiceRepository repository,
                              ServiceMapper serviceMapper) {
        this.repository = repository;
        this.serviceMapper = serviceMapper;
    }


    @Override
    public Service save(Service service) {
        return repository.save(service);
    }

    @Override
    public Optional<Service> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public ServiceResponseDto createService(ServiceRequestDto dto) {

        Service parent = null;
        if (dto.parentId() != null) {
            parent = repository.findById(dto.parentId())
                    .orElseThrow(() -> new NotFoundException("Parent not found"));
        }
        if (repository.existsByNameAndParentService(dto.name(), parent))
            throw new DuplicateException("Service name must be unique under the parent");

        Service entity = serviceMapper.toEntity(dto);
        entity.setParentService(parent);

        Service saved = repository.save(entity);
        return serviceMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ServiceResponseDto updateService(Long id, ServiceRequestDto dto) {
        Service service = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
        Service parent = null;
        if (dto.parentId() != null) {
            parent = repository.findById(dto.parentId())
                    .orElseThrow(() -> new NotFoundException("Parent not found"));
        }


        if (!service.getName().equals(dto.name()) ||
                !Objects.equals(
                        service.getParentService() == null ? null : service.getParentService().getId(),
                        dto.parentId())) {
            if (repository.existsByNameAndParentService(dto.name(), parent))
                throw new DuplicateException("Service name must be unique under the parent");
        }

        serviceMapper.updateEntityFromDto(dto, service);
        service.setParentService(parent);

        Service updated = repository.save(service);
        return serviceMapper.toDto(updated);
    }

    @Override
    public void deleteService(Long id) {
        Service service = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
        repository.delete(service);
    }

    @Override
    public List<ServiceResponseDto> listServices(Long parentId) {
        List<Service> services;
        if (parentId == null) {
            services = repository.findByParentService(null);
        } else {
            Service parent = repository.findById(parentId)
                    .orElseThrow(() -> new NotFoundException("Parent not found"));
            services = repository.findByParentService(parent);
        }
        return services.stream()
                .map(serviceMapper::toDto)
                .toList();
    }
}
