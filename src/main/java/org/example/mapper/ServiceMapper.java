package org.example.mapper;

import org.example.dto.service.ServiceRequestDto;
import org.example.dto.service.ServiceResponseDto;
import org.example.entity.Service;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    Service toEntity(ServiceRequestDto dto);

    ServiceResponseDto toDto(Service entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ServiceRequestDto dto, @MappingTarget Service entity);
}
