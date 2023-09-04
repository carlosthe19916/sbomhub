
package io.sbomhub.models.jpa.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "application_package")
public class ApplicationPackageEntity extends PanacheEntityBase {

    @EmbeddedId
    public Id id;

    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "name")
        public String name;

        @Column(name = "version")
        public String version;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(name, id.name) && Objects.equals(version, id.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, version);
        }
    }

    @ManyToMany(mappedBy = "packages", fetch = FetchType.LAZY)
    public List<SbomEntity> sboms = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationPackageEntity that = (ApplicationPackageEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
