package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.sbomhub.dto.SbomStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "sbom")
public class SbomEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "sbomb_sequence")
    public Long id;

    @NotBlank
    @Column(name = "file_id")
    public String fileId;

    @NotBlank
    @Column(name = "tag")
    public String tag;

    @Column(name = "packages_size")
    public Integer packages_size;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public SbomStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository")
    public RepositoryEntity repository;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "sbom")
    public List<ApplicationPackageEntity> packages = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SbomEntity that = (SbomEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
