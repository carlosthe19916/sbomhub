package io.sbomhub.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@RegisterForReflection
public record SearchResultBean<T>(List<T> data, long total) {

    public static <T> SearchResultBean<T> getEmptyResult(int offset, int limit) {
        return new SearchResultBean<>(Collections.emptyList(), 0L);
    }

    public static <I, O> SearchResultBean<O> transformData(
            SearchResultBean<I> searchResultBean,
            Function<I, O> function
    ) {
        List<O> data = searchResultBean.data.stream()
                .map(function)
                .toList();
        return new SearchResultBean<>(data, searchResultBean.total());
    }

}
