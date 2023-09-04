package io.sbomhub.models;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sbomhub.models.jpa.entity.SbomEntity;

import java.util.List;

@RegisterForReflection
public record ApplicationPackageFilterBean(String filterText, List<SbomEntity> sboms) {
}
