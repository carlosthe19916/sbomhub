package io.sbomhub.mapper;

import io.sbomhub.dto.RepositoryDto;
import io.sbomhub.models.jpa.entity.RepositoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface GitRepositoryMapper {

    RepositoryDto toDto(RepositoryEntity entity);

}
