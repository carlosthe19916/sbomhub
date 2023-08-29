package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.sbomhub.dto.RepositoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repository")
public class RepositoryEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "repository_sequence")
    public Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    public RepositoryType type;

    @NotBlank
    @Column(name = "url")
    public String url;

    @NotBlank
    @Column(name = "task_image")
    public String taskImage;

    @Embedded
    public GitDetailsEntity gitDetails;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product")
    public ProductEntity product;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "repository")
    public List<TaskEntity> tasks = new ArrayList<>();
}
