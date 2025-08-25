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
    void createService_success_withoutParent() {
        // Arrange
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.parentId()).thenReturn(null);

        Service entity = new Service();
        ServiceResponseDto resDto = mock(ServiceResponseDto.class);

        when(serviceMapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(serviceMapper.toDto(entity)).thenReturn(resDto);

        // Act
        ServiceResponseDto result = serviceService.createService(dto);

        // Assert
        assertSame(resDto, result);
        assertNull(entity.getParentService());
        verify(repository, never()).findById(anyLong());
        verify(repository).save(entity);
        verify(serviceMapper).toEntity(dto);
        verify(serviceMapper).toDto(entity);
    }

    @Test
    void createService_success_withParent() {
        // Arrange
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.parentId()).thenReturn(100L);

        Service parent = new Service();
        parent.setId(100L);

        Service entity = new Service();
        ServiceResponseDto resDto = mock(ServiceResponseDto.class);

        when(repository.findById(100L)).thenReturn(Optional.of(parent));
        when(serviceMapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(serviceMapper.toDto(entity)).thenReturn(resDto);

        // Act
        ServiceResponseDto result = serviceService.createService(dto);

        // Assert
        assertSame(resDto, result);
        assertSame(parent, entity.getParentService());
        verify(repository).findById(100L);
        verify(repository).save(entity);
    }

    @Test
    void createService_fail_ifParentNotFound() {
        // Arrange
        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.parentId()).thenReturn(200L);
        when(repository.findById(200L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> serviceService.createService(dto));
        verify(repository).findById(200L);
        verify(repository, never()).save(any());
    }







    @Test
    void updateService_success_noChange_skipUniqueness() {
        // Arrange
        Long serviceId = 1L;
        Service existing = new Service();
        existing.setId(serviceId);
        existing.setName("OldName");
        existing.setParentService(null);

        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.parentId()).thenReturn(null);
        when(dto.name()).thenReturn("OldName");

        when(repository.findById(serviceId)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        ServiceResponseDto resDto = mock(ServiceResponseDto.class);
        when(serviceMapper.toDto(existing)).thenReturn(resDto);

        // Act
        ServiceResponseDto result = serviceService.updateService(serviceId, dto);

        // Assert
        assertSame(resDto, result);
        verify(repository, never()).existsByNameAndParentService(any(), any());
    }

    @Test
    void updateService_success_withChange_noDuplicate() {
        Long serviceId = 2L;
        Service existing = new Service();
        existing.setId(serviceId);
        existing.setName("OldName");
        existing.setParentService(null);

        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.name()).thenReturn("NewName");
        when(dto.parentId()).thenReturn(null);

        when(repository.findById(serviceId)).thenReturn(Optional.of(existing));
        when(repository.existsByNameAndParentService(eq("NewName"), isNull())).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);
        ServiceResponseDto resDto = mock(ServiceResponseDto.class);
        when(serviceMapper.toDto(existing)).thenReturn(resDto);

        ServiceResponseDto result = serviceService.updateService(serviceId, dto);

        assertSame(resDto, result);
        verify(repository).existsByNameAndParentService("NewName", null);
        verify(serviceMapper).updateEntityFromDto(dto, existing);
    }

    @Test
    void updateService_fail_ifServiceNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        ServiceRequestDto dto = mock(ServiceRequestDto.class);

        assertThrows(NotFoundException.class, () -> serviceService.updateService(999L, dto));
    }

    @Test
    void updateService_fail_ifParentNotFound() {
        Long serviceId = 3L;
        Service existing = new Service();
        existing.setId(serviceId);
        existing.setName("OldName");

        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.parentId()).thenReturn(10L);

        when(repository.findById(serviceId)).thenReturn(Optional.of(existing));
        when(repository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serviceService.updateService(serviceId, dto));
    }

    @Test
    void updateService_fail_ifDuplicateFound() {
        Long serviceId = 4L;
        Service existing = new Service();
        existing.setId(serviceId);
        existing.setName("OldName");
        existing.setParentService(null);

        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.name()).thenReturn("NewName");
        when(dto.parentId()).thenReturn(null);

        when(repository.findById(serviceId)).thenReturn(Optional.of(existing));
        when(repository.existsByNameAndParentService("NewName", null)).thenReturn(true);

        assertThrows(DuplicateException.class, () -> serviceService.updateService(serviceId, dto));
    }

    @Test
    void updateService_success_changeOnlyParent() {
        Long serviceId = 5L;
        Service existing = new Service();
        existing.setId(serviceId);
        existing.setName("SameName");
        Service oldParent = new Service(); oldParent.setId(1L);
        existing.setParentService(oldParent);

        Service newParent = new Service(); newParent.setId(2L);

        ServiceRequestDto dto = mock(ServiceRequestDto.class);
        when(dto.name()).thenReturn("SameName");
        when(dto.parentId()).thenReturn(2L);

        when(repository.findById(serviceId)).thenReturn(Optional.of(existing));
        when(repository.findById(2L)).thenReturn(Optional.of(newParent));
        when(repository.existsByNameAndParentService("SameName", newParent)).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);
        ServiceResponseDto resDto = mock(ServiceResponseDto.class);
        when(serviceMapper.toDto(existing)).thenReturn(resDto);

        ServiceResponseDto result = serviceService.updateService(serviceId, dto);

        assertSame(resDto, result);
        assertSame(newParent, existing.getParentService());
    }








    @Test
    void deleteService_success_whenExists() {
        // Arrange
        Long serviceId = 1L;
        Service service = new Service();
        service.setId(serviceId);

        when(repository.findById(serviceId)).thenReturn(Optional.of(service));

        // Act
        serviceService.deleteService(serviceId);

        // Assert
        verify(repository).findById(serviceId);
        verify(repository).delete(service);
    }

    @Test
    void deleteService_fail_whenNotFound() {
        // Arrange
        Long serviceId = 99L;
        when(repository.findById(serviceId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> serviceService.deleteService(serviceId));

        verify(repository).findById(serviceId);
        verify(repository, never()).delete(any());
    }








    @Test
    void listServices_success_whenParentIdNull() {
        // Arrange
        Service service1 = new Service(); service1.setId(1L);
        Service service2 = new Service(); service2.setId(2L);

        ServiceResponseDto dto1 = mock(ServiceResponseDto.class);
        ServiceResponseDto dto2 = mock(ServiceResponseDto.class);

        when(repository.findByParentService(null)).thenReturn(List.of(service1, service2));
        when(serviceMapper.toDto(service1)).thenReturn(dto1);
        when(serviceMapper.toDto(service2)).thenReturn(dto2);

        // Act
        List<ServiceResponseDto> result = serviceService.listServices(null);

        // Assert
        assertEquals(List.of(dto1, dto2), result);
        verify(repository).findByParentService(null);
    }

    @Test
    void listServices_success_whenParentExists() {
        // Arrange
        Long parentId = 10L;
        Service parent = new Service(); parent.setId(parentId);

        Service service1 = new Service(); service1.setId(1L);

        ServiceResponseDto dto1 = mock(ServiceResponseDto.class);

        when(repository.findById(parentId)).thenReturn(Optional.of(parent));
        when(repository.findByParentService(parent)).thenReturn(List.of(service1));
        when(serviceMapper.toDto(service1)).thenReturn(dto1);

        // Act
        List<ServiceResponseDto> result = serviceService.listServices(parentId);

        // Assert
        assertEquals(List.of(dto1), result);
        verify(repository).findById(parentId);
        verify(repository).findByParentService(parent);
    }

    @Test
    void listServices_fail_whenParentNotFound() {
        // Arrange
        Long parentId = 20L;
        when(repository.findById(parentId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> serviceService.listServices(parentId));

        verify(repository).findById(parentId);
        verify(repository, never()).findByParentService(any());
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
