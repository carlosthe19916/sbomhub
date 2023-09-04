package io.sbomhub.dto;

public record PackageWithVersionCountDto(
        String name,
        Long count
) {
}
