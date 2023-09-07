package io.sbomhub.sbom;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sbomhub.dto.SbomStatus;
import io.sbomhub.models.jpa.entity.ApplicationPackageEntity;
import io.sbomhub.models.jpa.entity.SbomEntity;
import io.sbomhub.sbom.models.PackageJsonNode;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
@Named("sbomBean")
@RegisterForReflection
public class SbomBean {

    private static final Logger LOG = Logger.getLogger(SbomBean.class);

    @Blocking
    @Transactional
    public void fetchSbomAndUpdateStatusToStartProcessing(
            @Header(SbomRoute.SBOM_ID) Long sbomId,
            Exchange exchange
    ) {
        SbomEntity sbomEntity = SbomEntity.findById(sbomId);
        sbomEntity.status = SbomStatus.PROCESSING;
        sbomEntity.persist();

        exchange.getIn().setHeader(SbomRoute.SBOM_FILE_ID, sbomEntity.fileId);
    }

    @Blocking
    @Transactional
    public void updateSbomAndSetExpectedPackagesCount(
            @Header(SbomRoute.SBOM_ID) Long sbomId,
            Exchange exchange
    ) {
        SbomEntity sbomEntity = SbomEntity.findById(sbomId);
        sbomEntity.packages_size = exchange.getProperty(Exchange.SPLIT_SIZE, Integer.class);

        long total = ApplicationPackageEntity.count("sbom", sbomEntity);
        if (total >= sbomEntity.packages_size) {
            sbomEntity.status = SbomStatus.COMPLETED;
        }

        sbomEntity.persist();
    }

    @Blocking
    @Transactional
    public void savePackages(
            @Header(SbomRoute.SBOM_ID) Long sbomId,
            @Body List<PackageJsonNode> jsons
    ) {
        SbomEntity sbomEntity = SbomEntity.findById(sbomId);

        Stream<ApplicationPackageEntity> entityStream = jsons.stream().map(packageJsonNode -> {
            ApplicationPackageEntity entity = new ApplicationPackageEntity();
            entity.name = packageJsonNode.name();
            entity.version = packageJsonNode.versionInfo();
            entity.sbom = sbomEntity;
            return entity;
        });

        ApplicationPackageEntity.persist(entityStream);

        if (sbomEntity.packages_size != null) {
            long total = ApplicationPackageEntity.count("sbom", sbomEntity);
            if (total >= sbomEntity.packages_size) {
                sbomEntity.status = SbomStatus.COMPLETED;
                sbomEntity.persist();
            }
        }

        EntityManager entityManager = ApplicationPackageEntity.getEntityManager();
        entityManager.flush();
        entityManager.clear();
    }

}
