package io.sbomhub.mapper;

import io.sbomhub.dto.PackageWithVersionCountDto;
import io.sbomhub.models.jpa.PackageWithVersionCountProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ApplicationPackageMapper {

    PackageWithVersionCountDto toDto(PackageWithVersionCountProjection entity);

}