package io.sbomhub.mapper;

import io.sbomhub.dto.OrganizationDto;
import io.sbomhub.models.jpa.entity.OrganizationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface OrganizationMapper {

    OrganizationDto toDto(OrganizationEntity entity);

}
