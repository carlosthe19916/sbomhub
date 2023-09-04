package io.sbomhub.resources;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.sbomhub.dto.OrganizationDto;
import io.sbomhub.dto.RepositoryDto;
import io.sbomhub.dto.SbomDto;
import io.sbomhub.dto.SbomStatus;
import io.sbomhub.mapper.OrganizationMapper;
import io.sbomhub.mapper.RepositoryMapper;
import io.sbomhub.mapper.SbomMapper;
import io.sbomhub.models.jpa.entity.OrganizationEntity;
import io.sbomhub.models.jpa.entity.RepositoryEntity;
import io.sbomhub.models.jpa.entity.SbomEntity;
import io.sbomhub.resources.models.SbomFileForm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@ApplicationScoped
@Path("/organizations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrganizationsResource {

    @ConfigProperty(name = "storage.type")
    String storageType;

    @Inject
    OrganizationMapper organizationMapper;

    @Inject
    RepositoryMapper repositoryMapper;

    @Inject
    SbomMapper sbomMapper;

    @Inject
    ProducerTemplate producerTemplate;

    @POST
    @Path("/")
    public OrganizationDto createOrganization(OrganizationDto dto) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.name = dto.name();
        entity.description = dto.description();
        entity.persist();

        return organizationMapper.toDto(entity);
    }

    @GET
    @Path("/")
    public List<OrganizationDto> listOrganization() {
        return OrganizationEntity.<OrganizationEntity>listAll().stream()
                .map(entity -> organizationMapper.toDto(entity))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{organization}")
    public RestResponse<OrganizationDto> getOrganization(@PathParam("organization") String organization) {
        return OrganizationEntity.<OrganizationEntity>findByIdOptional(organization)
                .map(entity -> organizationMapper.toDto(entity))
                .map(dto -> RestResponse.ResponseBuilder
                        .<OrganizationDto>create(RestResponse.Status.OK)
                        .entity(dto)
                        .build()
                )
                .orElse(RestResponse.ResponseBuilder
                        .<OrganizationDto>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

    @PUT
    @Path("/{organization}")
    public RestResponse<OrganizationDto> updateOrganization(@PathParam("organization") String organization, OrganizationDto organizationDto) {
        return OrganizationEntity.<OrganizationEntity>findByIdOptional(organization)
                .map(entity -> {
                    entity.description = organizationDto.description();
                    entity.persist();

                    return organizationMapper.toDto(entity);
                })
                .map(dto -> RestResponse.ResponseBuilder
                        .<OrganizationDto>create(RestResponse.Status.OK)
                        .entity(dto)
                        .build()
                )
                .orElse(RestResponse.ResponseBuilder
                        .<OrganizationDto>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

    @DELETE
    @Path("/{organization}")
    public RestResponse<Void> deleteOrganization(@PathParam("organization") String organization) {

        return OrganizationEntity.<OrganizationEntity>findByIdOptional(organization)
                .map(entity -> {
                    entity.delete();
                    return true;
                })
                .map(result -> RestResponse.ResponseBuilder
                        .<Void>create(RestResponse.Status.OK)
                        .build()
                )
                .orElse(RestResponse.ResponseBuilder
                        .<Void>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

    @POST
    @Path("/{organization}/repositories")
    public RestResponse<RepositoryDto> createRepository(@PathParam("organization") String organization, RepositoryDto dto) {
        Optional<OrganizationEntity> product = OrganizationEntity.findByIdOptional(organization);
        if (product.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<RepositoryDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        OrganizationEntity organizationEntity = product.get();

        RepositoryEntity repositoryEntity = new RepositoryEntity();
        repositoryEntity.name = dto.name();
        repositoryEntity.description = dto.description();
        repositoryEntity.organization = organizationEntity;
        repositoryEntity.persist();

        RepositoryDto result = repositoryMapper.toDto(repositoryEntity);
        return RestResponse.ResponseBuilder
                .<RepositoryDto>create(RestResponse.Status.OK)
                .entity(result)
                .build();
    }

    @GET
    @Path("/{organization}/repositories")
    public RestResponse<List<RepositoryDto>> getRepositories(@PathParam("organization") String organization) {
        return OrganizationEntity.<OrganizationEntity>findByIdOptional(organization)
                .map(productEntity -> {
                    return productEntity.repositories.stream()
                            .map(gitRepositoryEntity -> repositoryMapper.toDto(gitRepositoryEntity))
                            .sorted(Comparator.comparing(RepositoryDto::name))
                            .collect(Collectors.toList());
                })
                .map(dtos -> {
                    return RestResponse.ResponseBuilder
                            .<List<RepositoryDto>>create(RestResponse.Status.OK)
                            .entity(dtos)
                            .build();
                })
                .orElse(RestResponse.ResponseBuilder
                        .<List<RepositoryDto>>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

    @Transactional(Transactional.TxType.NEVER)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    @Path("/{organization}/repositories/{repository}/sboms")
    public RestResponse<SbomDto> createSbom(@PathParam("organization") String organization, @PathParam("repository") String repository, @BeanParam SbomFileForm sbomFileForm) {
        // Upload file
        Map<String, Object> headers = new HashMap<>();

        File body = sbomFileForm.file.filePath().toFile();
        String fileId = producerTemplate.requestBodyAndHeaders("direct:" + storageType + "-save-file", body, headers, String.class);

        // Start process
        QuarkusTransaction.begin();

        Optional<OrganizationEntity> organizationEntityOptional = OrganizationEntity.findByIdOptional(organization);
        if (organizationEntityOptional.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<SbomDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        Optional<RepositoryEntity> optionalRepositoryEntity = RepositoryEntity.findByIdOptional(repository);
        if (optionalRepositoryEntity.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<SbomDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        if (!Objects.equals(organizationEntityOptional.get(), optionalRepositoryEntity.get().organization)) {
            return RestResponse.ResponseBuilder
                    .<SbomDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        SbomEntity sbomEntity = new SbomEntity();
        sbomEntity.status = SbomStatus.SCHEDULED;
        sbomEntity.fileId = fileId;
        sbomEntity.tag = sbomFileForm.tag;
        sbomEntity.repository = optionalRepositoryEntity.get();
        sbomEntity.persist();

        SbomDto result = sbomMapper.toDto(sbomEntity);

        QuarkusTransaction.commit();

        // Schedule SBOM analysis
        producerTemplate.requestBodyAndHeaders("direct:schedule-sbom-analysis", result.id(), null);

        return RestResponse.ResponseBuilder
                .<SbomDto>create(RestResponse.Status.OK)
                .entity(result)
                .build();
    }

    @GET
    @Path("/{organization}/repositories/{repository}/sboms")
    public RestResponse<List<SbomDto>> getSboms(@PathParam("organization") String organization, @PathParam("repository") String repository) {
        Optional<OrganizationEntity> organizationEntityOptional = OrganizationEntity.findByIdOptional(organization);
        if (organizationEntityOptional.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<List<SbomDto>>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        Optional<RepositoryEntity> optionalRepositoryEntity = RepositoryEntity.findByIdOptional(repository);
        if (optionalRepositoryEntity.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<List<SbomDto>>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        if (!Objects.equals(organizationEntityOptional.get(), optionalRepositoryEntity.get().organization)) {
            return RestResponse.ResponseBuilder
                    .<List<SbomDto>>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        List<SbomDto> result = optionalRepositoryEntity.get().sboms.stream()
                .map(sbomEntity -> sbomMapper.toDto(sbomEntity))
                .collect(Collectors.toList());

        return RestResponse.ResponseBuilder
                .<List<SbomDto>>create(RestResponse.Status.OK)
                .entity(result)
                .build();
    }

}
