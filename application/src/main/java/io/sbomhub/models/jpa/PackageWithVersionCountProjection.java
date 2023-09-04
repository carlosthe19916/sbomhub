package io.sbomhub.models.jpa;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record PackageWithVersionCountProjection(
        @ProjectedFieldName("name") String name,
        @ProjectedFieldName("version_count") Long count
) {
}
