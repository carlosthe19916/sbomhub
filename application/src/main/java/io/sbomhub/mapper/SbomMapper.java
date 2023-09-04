package io.sbomhub.mapper;

import io.sbomhub.dto.SbomDto;
import io.sbomhub.models.jpa.entity.SbomEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface SbomMapper {

    SbomDto toDto(SbomEntity entity);

}
