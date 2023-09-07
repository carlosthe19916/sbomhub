package io.sbomhub.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RegisterForReflection
public record SortBean(String fieldName, boolean asc) {

    public static List<SortBean> buildWith(List<String> sortBy, String... validFieldNames) {
        if (sortBy == null) {
            return Collections.emptyList();
        }
        List<String> validFieldNamesList = validFieldNames != null ? Arrays.asList(validFieldNames) : Collections.emptyList();
        return sortBy.stream()
                .flatMap(f -> Stream.of(f.split(",")))
                .map(f -> {
                    String[] split = f.trim().split(":");
                    String fieldName = !split[1].isEmpty() ? split[1] : null;
                    boolean isAsc = split[0].equalsIgnoreCase("asc");
                    return new SortBean(fieldName, isAsc);
                })
                .filter(f -> validFieldNamesList.contains(f.fieldName()))
                .collect(Collectors.toList());
    }
}
