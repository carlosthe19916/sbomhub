package io.sbomhub.models;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sbomhub.models.jpa.entity.SbomEntity;

@RegisterForReflection
public record ApplicationPackageFilterBean(String filterText, SbomEntity sbom) {
}
