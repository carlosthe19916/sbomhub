package io.sbomhub.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.List;

@RegisterForReflection
public record SearchResultDto<T>(Meta meta, List<T> data) {

    public static <T> SearchResultDto<T> getEmptyResult(int offset, int limit) {
        Meta meta = new Meta(offset, limit, 0L);
        return new SearchResultDto<>(meta, Collections.emptyList());
    }

    @RegisterForReflection
    public record Meta(Integer offset, Integer limit, Long count) {
    }

}
