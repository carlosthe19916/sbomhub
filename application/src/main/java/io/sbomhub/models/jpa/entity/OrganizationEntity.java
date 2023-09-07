package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "organization", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class OrganizationEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "organization_sequence")
    public Long id;

    @NotBlank
    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "organization")
    public List<RepositoryEntity> repositories = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationEntity that = (OrganizationEntity) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
