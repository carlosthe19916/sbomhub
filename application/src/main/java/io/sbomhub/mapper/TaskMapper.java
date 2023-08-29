package io.sbomhub.mapper;

import io.sbomhub.dto.TaskDto;
import io.sbomhub.models.jpa.entity.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface TaskMapper {

    TaskDto toDto(TaskEntity entity);

}
