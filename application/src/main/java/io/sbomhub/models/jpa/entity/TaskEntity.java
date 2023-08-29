package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.sbomhub.dto.TaskState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task")
public class TaskEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "task_sequence")
    public Long id;

    @NotBlank
    @Column(name = "name")
    public String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    public TaskState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository")
    public RepositoryEntity repository;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "task")
    public List<SbomEntity> sboms = new ArrayList<>();
}
