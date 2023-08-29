package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class ProductEntity extends PanacheEntityBase {

    @Id
    public String name;

    @Column(name = "description")
    public String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "product")
    public List<RepositoryEntity> repositories = new ArrayList<>();

}
