package io.sbomhub.resources;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.sbomhub.dto.RepositoryDto;
import io.sbomhub.dto.SbomDto;
import io.sbomhub.dto.SbomStatus;
import io.sbomhub.mapper.RepositoryMapper;
import io.sbomhub.mapper.SbomMapper;
import io.sbomhub.models.PageBean;
import io.sbomhub.models.SearchResultBean;
import io.sbomhub.models.SortBean;
import io.sbomhub.models.jpa.RepositoryRepository;
import io.sbomhub.models.jpa.entity.OrganizationEntity;
import io.sbomhub.models.jpa.entity.RepositoryEntity;
import io.sbomhub.models.jpa.entity.SbomEntity;
import io.sbomhub.resources.models.QueryUtils;
import io.sbomhub.resources.models.SbomFileForm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
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
@Path("/repositories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RepositoriesResource {

    @ConfigProperty(name = "storage.type")
    String storageType;

    @Inject
    RepositoryRepository repositoryRepository;

    @Inject
    RepositoryMapper repositoryMapper;

    @Inject
    SbomMapper sbomMapper;

    @Inject
    ProducerTemplate producerTemplate;

    @POST
    @Path("/")
    public RestResponse<RepositoryDto> createRepository(RepositoryDto dto) {
        Optional<OrganizationEntity> organizationEntityOptional = OrganizationEntity.findByIdOptional(dto.organization().id());
        if (organizationEntityOptional.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<RepositoryDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        Optional<RepositoryEntity> repositoryEntityOptional = RepositoryEntity.find("organization = :organization and name = :name", Map.of(
                "organization", organizationEntityOptional.get(),
                "name", dto.name()
        )).singleResultOptional();
        if (repositoryEntityOptional.isPresent()) {
            return RestResponse.ResponseBuilder
                    .<RepositoryDto>create(RestResponse.Status.BAD_REQUEST)
                    .build();
        }

        RepositoryEntity repositoryEntity = new RepositoryEntity();
        repositoryEntity.name = dto.name();
        repositoryEntity.description = dto.description();
        repositoryEntity.organization = organizationEntityOptional.get();
        repositoryEntity.persist();

        RepositoryDto result = repositoryMapper.toDto(repositoryEntity);
        return RestResponse.ResponseBuilder
                .<RepositoryDto>create(RestResponse.Status.OK)
                .entity(result)
                .build();
    }

    @GET
    @Path("/")
    public SearchResultBean<RepositoryDto> getRepositories(
            @QueryParam("q") String filterText,
            @QueryParam("offset") @DefaultValue("0") @Max(9_000) Integer offset,
            @QueryParam("limit") @DefaultValue("10") @Max(1_000) Integer limit,
            @QueryParam("sort") List<String> sort,
            @QueryParam("organization") @DefaultValue("") String organization
    ) {
        PageBean pageBean = PageBean.buildWith(offset, limit);
        List<SortBean> sortBeans = SortBean.buildWith(sort, RepositoryRepository.SORT_BY_FIELDS);

        List<OrganizationEntity> organizationEntities = QueryUtils.extractQuery(organization, Long::valueOf).stream()
                .map(aLong -> {
                    OrganizationEntity byId = OrganizationEntity.findById(aLong);
                    return byId;
                })
                .filter(Objects::nonNull)
                .toList();
        RepositoryRepository.FilterBean filterBean = new RepositoryRepository.FilterBean(Optional.ofNullable(filterText), organizationEntities);

        SearchResultBean<RepositoryEntity> searchResult = repositoryRepository.list(filterBean, pageBean, sortBeans);
        return SearchResultBean.transformData(searchResult, entity -> repositoryMapper.toDto(entity));
    }

    @GET
    @Path("/{repositoryId}")
    public RestResponse<RepositoryDto> getRepository(@PathParam("repositoryId") Long repositoryId) {
        return RepositoryEntity.<RepositoryEntity>findByIdOptional(repositoryId)
                .map(entity -> repositoryMapper.toDto(entity))
                .map(dto -> RestResponse.ResponseBuilder
                        .<RepositoryDto>create(RestResponse.Status.OK)
                        .entity(dto)
                        .build()
                )
                .orElse(RestResponse.ResponseBuilder
                        .<RepositoryDto>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

    @PUT
    @Path("/{repositoryId}")
    public RestResponse<RepositoryDto> updateRepository(@PathParam("repositoryId") Long repositoryId, RepositoryDto repositoryDto) {
        return RepositoryEntity.<RepositoryEntity>findByIdOptional(repositoryId)
                .map(entity -> {
                    entity.name = repositoryDto.name();
                    entity.description = repositoryDto.description();
                    entity.persist();

                    return repositoryMapper.toDto(entity);
                })
                .map(dto -> RestResponse.ResponseBuilder
                        .<RepositoryDto>create(RestResponse.Status.OK)
                        .entity(dto)
                        .build()
                )
                .orElse(RestResponse.ResponseBuilder
                        .<RepositoryDto>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

    @DELETE
    @Path("/{repositoryId}")
    public RestResponse<Void> deleteRepository(@PathParam("repositoryId") Long repositoryId) {
        return RepositoryEntity.<RepositoryEntity>findByIdOptional(repositoryId)
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

    @Transactional(Transactional.TxType.NEVER)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @POST
    @Path("/{repository}/tags")
    public RestResponse<SbomDto> createTag(@PathParam("repository") String repository, @BeanParam SbomFileForm sbomFileForm) {
        // Upload file
        Map<String, Object> headers = new HashMap<>();

        File body = sbomFileForm.file.filePath().toFile();
        String fileId = producerTemplate.requestBodyAndHeaders("direct:" + storageType + "-save-file", body, headers, String.class);

        // Start process
        QuarkusTransaction.begin();

        Optional<RepositoryEntity> optionalRepositoryEntity = RepositoryEntity.findByIdOptional(repository);
        if (optionalRepositoryEntity.isEmpty()) {
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
    @Path("/{repositoryId}/tags")
    public RestResponse<List<SbomDto>> getTags(@PathParam("repositoryId") String repositoryId) {
        Optional<RepositoryEntity> optionalRepositoryEntity = RepositoryEntity.findByIdOptional(repositoryId);
        if (optionalRepositoryEntity.isEmpty()) {
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
