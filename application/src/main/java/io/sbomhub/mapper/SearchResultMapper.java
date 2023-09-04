package io.sbomhub.mapper;

import io.sbomhub.dto.SearchResultDto;
import io.sbomhub.models.SearchResultBean;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchResultMapper {
    private SearchResultMapper() {
        // Just static methods
    }

    public <I, O> SearchResultDto<O> toDto(
            SearchResultBean<I> bean,
            Function<I, O> function
    ) {
        SearchResultDto.Meta meta = new SearchResultDto.Meta(
                bean.offset(),
                bean.limit(),
                bean.totalElements()
        );
        List<O> collect = bean.pageElements().stream()
                .map(function)
                .collect(Collectors.toList());
        return new SearchResultDto<>(meta, collect);
    }

}
