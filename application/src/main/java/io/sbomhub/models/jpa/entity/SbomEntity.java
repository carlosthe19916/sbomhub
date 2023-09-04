package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.sbomhub.dto.SbomStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.*;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public SbomStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository")
    public RepositoryEntity repository;

    @ManyToMany(
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "sbom_package",
            joinColumns = {
                    @JoinColumn(name = "sbom_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "package_name"),
                    @JoinColumn(name = "package_version")
            }
    )
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
