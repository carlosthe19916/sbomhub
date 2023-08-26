package io.sbomhub.resources;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.sbomhub.dto.GitRepositoryDto;
import io.sbomhub.dto.GitTaskDto;
import io.sbomhub.dto.ProductDto;
import io.sbomhub.mapper.GitRepositoryMapper;
import io.sbomhub.mapper.GitTaskMapper;
import io.sbomhub.mapper.ProductMapper;
import io.sbomhub.models.TaskState;
import io.sbomhub.models.jpa.entity.GitRepositoryEntity;
import io.sbomhub.models.jpa.entity.GitTaskEntity;
import io.sbomhub.models.jpa.entity.ProductEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@ApplicationScoped
@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductMapper productMapper;

    @Inject
    GitRepositoryMapper gitRepositoryMapper;

    @Inject
    GitTaskMapper gitTaskMapper;

    @Inject
    Event<GitTaskDto> gitTaskEntityEvent;

    @POST
    @Path("/")
    public ProductDto createProduct(ProductDto dto) {
        ProductEntity entity = new ProductEntity();
        entity.name = dto.name();
        entity.description = dto.description();
        entity.persist();

        return productMapper.toDto(entity);
    }

    @GET
    @Path("/{productName}")
    public RestResponse<ProductDto> getProduct(@PathParam("productName") String productName) {
        return ProductEntity.<ProductEntity>findByIdOptional(productName)
                .map(entity -> productMapper.toDto(entity))
                .map(dto -> RestResponse.ResponseBuilder
                        .<ProductDto>create(RestResponse.Status.OK)
                        .entity(dto)
                        .build()
                )
                .orElse(RestResponse.ResponseBuilder
                        .<ProductDto>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

    @POST
    @Path("/{productName}/repositories")
    public RestResponse<GitRepositoryDto> createRepository(@PathParam("productName") String productName, GitRepositoryDto dto) {
        Optional<ProductEntity> product = ProductEntity.<ProductEntity>findByIdOptional(productName);
        if (product.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<GitRepositoryDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        ProductEntity productEntity = product.get();

        GitRepositoryEntity gitRepositoryEntity = new GitRepositoryEntity();
        gitRepositoryEntity.url = dto.url();
        gitRepositoryEntity.ref = dto.ref();
        gitRepositoryEntity.rootPath = dto.rootPath();
        gitRepositoryEntity.product = productEntity;
        gitRepositoryEntity.persist();

        GitRepositoryDto result = gitRepositoryMapper.toDto(gitRepositoryEntity);
        return RestResponse.ResponseBuilder
                .<GitRepositoryDto>create(RestResponse.Status.OK)
                .entity(result)
                .build();
    }

    @GET
    @Path("/{productName}/repositories")
    public RestResponse<List<GitRepositoryDto>> getRepositories(@PathParam("productName") String productName) {
        return ProductEntity.<ProductEntity>findByIdOptional(productName)
                .map(productEntity -> {
                    return productEntity.repositories.stream()
                            .map(gitRepositoryEntity -> gitRepositoryMapper.toDto(gitRepositoryEntity))
                            .sorted(Comparator.comparing(GitRepositoryDto::url))
                            .collect(Collectors.toList());
                })
                .map(dtos -> {
                    return RestResponse.ResponseBuilder
                            .<List<GitRepositoryDto>>create(RestResponse.Status.OK)
                            .entity(dtos)
                            .build();
                })
                .orElse(RestResponse.ResponseBuilder
                        .<List<GitRepositoryDto>>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

    @Transactional(Transactional.TxType.NEVER)
    @POST
    @Path("/{productName}/repositories/{repositoryId}/tasks")
    public RestResponse<GitTaskDto> createTask(
            @PathParam("productName") String productName,
            @PathParam("repositoryId") Long repositoryId
    ) {
        QuarkusTransaction.begin();

        Optional<ProductEntity> productEntityOptional = ProductEntity.<ProductEntity>findByIdOptional(productName);
        if (productEntityOptional.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<GitTaskDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        Optional<GitRepositoryEntity> gitRepositoryEntityOptional = GitRepositoryEntity.<GitRepositoryEntity>findByIdOptional(repositoryId);
        if (gitRepositoryEntityOptional.isPresent() && !gitRepositoryEntityOptional.get().product.name.equals(productEntityOptional.get().name)) {
            return RestResponse.ResponseBuilder
                    .<GitTaskDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }


        RestResponse<GitTaskDto> result = gitRepositoryEntityOptional
                .map(gitRepositoryEntity -> {
                    GitTaskEntity gitTaskEntity = new GitTaskEntity();
                    gitTaskEntity.state = TaskState.Created;
                    gitTaskEntity.gitRepository = gitRepositoryEntity;
                    gitTaskEntity.persist();

                    return gitTaskMapper.toDto(gitTaskEntity);
                })
                .map(dto -> {
                    return RestResponse.ResponseBuilder
                            .<GitTaskDto>create(RestResponse.Status.OK)
                            .entity(dto)
                            .build();
                })
                .orElse(RestResponse.ResponseBuilder
                        .<GitTaskDto>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );

        QuarkusTransaction.commit();
        gitTaskEntityEvent.fire(result.getEntity());

        return result;
    }

}
