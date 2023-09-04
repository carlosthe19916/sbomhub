package io.sbomhub.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public record SearchResultBean<T>(int offset, int limit, long totalElements, List<T> pageElements) {
}
