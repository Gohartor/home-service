package org.example.service;

import org.example.dto.service.ServiceRequestDto;
import org.example.dto.service.ServiceResponseDto;
import org.example.entity.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceService {
//    List<Service> findAll();
//    void deleteById(Long id);
//    boolean existsById(Long id);
//    Service createService(Service service);
//    List<Service> findByParentService(Service parentService);

    Service save(Service service);
    Optional<Service> findById(Long id);
    ServiceResponseDto createService(ServiceRequestDto dto);
    ServiceResponseDto updateService(Long serviceId, ServiceRequestDto dto);
    void deleteService(Long serviceId);
    List<ServiceResponseDto> listServices(Long parentId);

}