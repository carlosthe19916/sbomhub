package io.sbomhub.dto;

import java.util.List;

public record TaskDto(
        Long id,
        String name,
        TaskState state,
        RepositoryDto repository,
        List<SbomDto> sbombs
) {
}
