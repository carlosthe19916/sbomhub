
package io.sbomhub.sbom;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sbomhub.dto.SbomStatus;
import io.sbomhub.models.jpa.entity.ApplicationPackageEntity;
import io.sbomhub.models.jpa.entity.SbomEntity;
import io.sbomhub.sbom.models.PackageJsonNode;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("sbomBean")
@RegisterForReflection
public class SbomBean {

    @Blocking
    @Transactional
    public void fetchSbom(
            @Header(SbomRoute.SBOM_ID) Long sbomId,
            Exchange exchange
    ) {
        SbomEntity sbomEntity = SbomEntity.findById(sbomId);
        exchange.getIn().setHeader(SbomRoute.SBOM_FILE_ID, sbomEntity.fileId);
    }

    @Blocking
    @Transactional
    public void savePackage(
            @Header(SbomRoute.SBOM_ID) Long sbomId,
            @Body PackageJsonNode packageJsonNode
    ) {
        SbomEntity sbomEntity = SbomEntity.findById(sbomId);

        ApplicationPackageEntity applicationPackageEntity = new ApplicationPackageEntity();
        applicationPackageEntity.id = getId(packageJsonNode);
        applicationPackageEntity.persist();

        sbomEntity.packages.add(applicationPackageEntity);
        sbomEntity.persist();

//        System.out.println("Inserting");
    }

    @Blocking
    @Transactional
    public void savePackages(
            @Header(SbomRoute.SBOM_ID) Long sbomId,
            @Body List<PackageJsonNode> packages
    ) {
        SbomEntity sbomEntity = SbomEntity.findById(sbomId);

        List<ApplicationPackageEntity> newPackages = packages.stream()
                .map(packageJsonNode -> {
                    ApplicationPackageEntity applicationPackageEntity = new ApplicationPackageEntity();
                    applicationPackageEntity.id = getId(packageJsonNode);

                    applicationPackageEntity.persist();
                    return applicationPackageEntity;
                })
                .toList();

        sbomEntity.packages.addAll(newPackages);
        sbomEntity.persist();

        System.out.println("Inserting");
    }

    private ApplicationPackageEntity.Id getId(PackageJsonNode json) {
        ApplicationPackageEntity.Id id = new ApplicationPackageEntity.Id();
        id.name = json.name();
        id.version = json.versionInfo();
        return id;
    }

    @Blocking
    @Transactional
    public void updateSbomStatusToComplete(
            @Header(SbomRoute.SBOM_ID) Long sbomId
    ) {
        SbomEntity sbomEntity = SbomEntity.findById(sbomId);
        sbomEntity.status = SbomStatus.COMPLETED;
        sbomEntity.persist();

        System.out.println("COMPLETED");
    }
}
