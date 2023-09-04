package io.sbomhub.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record PageBean(int offset, int limit) {
    public static PageBean buildWith(Integer offset, Integer limit) {
        if (offset == null || offset < 0) {
            offset = 0;
        }

        if (limit == null || limit > 1000) {
            limit = 1000;
        }
        if (limit < 0) {
            limit = 10;
        }

        return new PageBean(offset, limit);
    }
}
