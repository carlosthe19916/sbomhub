
package io.sbomhub.dto;

public record SbomDto(
        Long id,
        String tag,
        SbomStatus status
) {
}
