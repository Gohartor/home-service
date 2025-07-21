package org.example.service.impl;

import org.example.dto.service.ServiceRequestDto;
import org.example.dto.service.ServiceResponseDto;
import org.example.entity.Service;
import org.example.exception.DuplicateException;
import org.example.mapper.ServiceMapper;
import org.example.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.webjars.NotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceServiceImplTest {

    @Mock ServiceRepository repository;
    @Mock ServiceMapper serviceMapper;

    @InjectMocks ServiceServiceImpl serviceService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createService_success_withParent() {
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.name()).thenReturn("Child");
        when(dto.parentId()).thenReturn(1L);

        Service parent = new Service();
        parent.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(parent));
        when(repository.existsByNameAndParentService("Child", parent)).thenReturn(false);

        Service mapped = new Service();
        mapped.setName("Child");

        when(serviceMapper.toEntity(dto)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(mapped);

        ServiceResponseDto resDto = mock(ServiceResponseDto.class);
        when(serviceMapper.toDto(mapped)).thenReturn(resDto);

        ServiceResponseDto result = serviceService.createService(dto);

        assertEquals(resDto, result);
        assertSame(parent, mapped.getParentService());
        verify(repository).save(mapped);
    }

    @Test
    void createService_fail_ifNameNotUnique() {
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.name()).thenReturn("Child");
        when(dto.parentId()).thenReturn(null);

        when(repository.existsByNameAndParentService("Child", null)).thenReturn(true);

        assertThrows(DuplicateException.class, () -> serviceService.createService(dto));
    }

    @Test
    void createService_fail_ifParentNotFound() {
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.name()).thenReturn("Child");
        when(dto.parentId()).thenReturn(99L);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serviceService.createService(dto));
    }



    @Test
    void updateService_success_changeNameAndParent() {
        // Arrange
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.name()).thenReturn("Updated");
        when(dto.parentId()).thenReturn(10L);

        Service service = new Service(); service.setId(5L); service.setName("Original");
        Service parent = new Service(); parent.setId(10L);

        when(repository.findById(5L)).thenReturn(Optional.of(service));
        when(repository.findById(10L)).thenReturn(Optional.of(parent));
        when(repository.existsByNameAndParentService("Updated", parent)).thenReturn(false);

        ServiceResponseDto responseDto = mock(ServiceResponseDto.class);
        when(repository.save(any())).thenReturn(service);
        when(serviceMapper.toDto(service)).thenReturn(responseDto);

        ServiceServiceImplTest.setName(service, "Original");

        // Act
        ServiceResponseDto result = serviceService.updateService(5L, dto);

        // Assert
        verify(serviceMapper).updateEntityFromDto(dto, service);
        assertEquals(parent, service.getParentService());
        assertEquals(responseDto, result);
    }

    @Test
    void updateService_fail_ifServiceNotFound() {
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(repository.findById(66L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> serviceService.updateService(66L, dto));
    }

    @Test
    void updateService_fail_ifParentNotFound() {
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.parentId()).thenReturn(42L);

        Service service = new Service();
        when(repository.findById(7L)).thenReturn(Optional.of(service));
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serviceService.updateService(7L, dto));
    }

    @Test
    void updateService_fail_ifNameDuplicate() {
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.name()).thenReturn("NewName");
        when(dto.parentId()).thenReturn(null);

        Service service = new Service(); service.setName("OldName");
        when(repository.findById(5L)).thenReturn(Optional.of(service));
        when(repository.existsByNameAndParentService("NewName", null)).thenReturn(true);

        assertThrows(DuplicateException.class, () -> serviceService.updateService(5L, dto));
    }


    @Test
    void deleteService_success() {
        Service service = new Service();
        when(repository.findById(3L)).thenReturn(Optional.of(service));

        serviceService.deleteService(3L);

        verify(repository).delete(service);
    }

    @Test
    void deleteService_fail_ifNotFound() {
        when(repository.findById(22L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> serviceService.deleteService(22L));
    }




    @Test
    void listServices_success_NoParent() {
        Service s1 = new Service(); s1.setId(1L);
        Service s2 = new Service(); s2.setId(2L);
        List<Service> services = Arrays.asList(s1, s2);
        when(repository.findByParentService(null)).thenReturn(services);

        ServiceResponseDto dto1 = mock(ServiceResponseDto.class);
        ServiceResponseDto dto2 = mock(ServiceResponseDto.class);
        when(serviceMapper.toDto(s1)).thenReturn(dto1);
        when(serviceMapper.toDto(s2)).thenReturn(dto2);

        List<ServiceResponseDto> result = serviceService.listServices(null);

        assertEquals(List.of(dto1, dto2), result);
    }

    @Test
    void listServices_success_WithParent() {
        Service parent = new Service(); parent.setId(20L);
        Service s1 = new Service(); s1.setId(3L); s1.setParentService(parent);
        when(repository.findById(20L)).thenReturn(Optional.of(parent));
        when(repository.findByParentService(parent)).thenReturn(List.of(s1));

        ServiceResponseDto dto = mock(ServiceResponseDto.class);
        when(serviceMapper.toDto(s1)).thenReturn(dto);

        List<ServiceResponseDto> result = serviceService.listServices(20L);

        assertEquals(List.of(dto), result);
    }

    @Test
    void listServices_fail_ifParentNotFound() {
        when(repository.findById(34L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> serviceService.listServices(34L));
    }

    // -----------------
    // findById (برای completeness)
    // -----------------
    @Test
    void findById_success() {
        Service s = new Service();
        ServiceResponseDto dto = mock(ServiceResponseDto.class);
        when(repository.findById(1L)).thenReturn(Optional.of(s));
        when(serviceMapper.toDto(s)).thenReturn(dto);

        Optional<ServiceResponseDto> res = serviceService.findById(1L);
        assertTrue(res.isPresent());
        assertEquals(dto, res.get());
    }

    @Test
    void findById_notFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertTrue(serviceService.findById(2L).isEmpty());
    }



    static void setName(Service service, String name) {
        try {
            var f = Service.class.getDeclaredField("name");
            f.setAccessible(true);
            f.set(service, name);
        } catch (Exception ignore) {}
    }
}
