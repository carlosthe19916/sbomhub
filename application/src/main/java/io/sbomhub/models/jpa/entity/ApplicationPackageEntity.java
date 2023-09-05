
package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "application_package")
public class ApplicationPackageEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "application_package_sequence")
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "version")
    public String version;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sbom")
    public SbomEntity sbom;

}
