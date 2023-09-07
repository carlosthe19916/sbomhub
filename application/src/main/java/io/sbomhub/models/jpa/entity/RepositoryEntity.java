package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repository", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization", "name"})
})
public class RepositoryEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "repository_sequence")
    public Long id;

    @NotNull
    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization")
    public OrganizationEntity organization;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "repository")
    public List<SbomEntity> sboms = new ArrayList<>();

}
