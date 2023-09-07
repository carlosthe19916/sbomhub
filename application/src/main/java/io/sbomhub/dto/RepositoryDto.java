
package io.sbomhub.dto;

public record RepositoryDto(
        Long id,
        String name,
        String description,
        OrganizationDto organization
) {
}
