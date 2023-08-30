package io.sbomhub.resources;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.sbomhub.dto.RepositoryDto;
import io.sbomhub.dto.ProductDto;
import io.sbomhub.dto.TaskDto;
import io.sbomhub.mapper.GitRepositoryMapper;
import io.sbomhub.mapper.ProductMapper;
import io.sbomhub.mapper.TaskMapper;
import io.sbomhub.models.jpa.entity.GitDetailsEntity;
import io.sbomhub.models.jpa.entity.RepositoryEntity;
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
    TaskMapper gitTaskMapper;

    @Inject
    Event<TaskDto> gitTaskEntityEvent;

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
    @Path("/")
    public List<ProductDto> listProducts() {
        return ProductEntity.<ProductEntity>listAll().stream()
                .map(entity -> productMapper.toDto(entity))
                .collect(Collectors.toList());
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

    @PUT
    @Path("/{productName}")
    public RestResponse<ProductDto> updateProduct(@PathParam("productName") String productName, ProductDto productDto) {
        return ProductEntity.<ProductEntity>findByIdOptional(productName)
                .map(entity -> {
                    entity.description = productDto.description();
                    entity.persist();

                    return productMapper.toDto(entity);
                })
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
    public RestResponse<RepositoryDto> createRepository(@PathParam("productName") String productName, RepositoryDto dto) {
        Optional<ProductEntity> product = ProductEntity.<ProductEntity>findByIdOptional(productName);
        if (product.isEmpty()) {
            return RestResponse.ResponseBuilder
                    .<RepositoryDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        ProductEntity productEntity = product.get();

        RepositoryEntity repositoryEntity = new RepositoryEntity();
        repositoryEntity.type = dto.type();
        repositoryEntity.url = dto.url();
        repositoryEntity.taskImage = dto.taskImage();
        repositoryEntity.product = productEntity;
        if (dto.gitDetails() != null) {
            repositoryEntity.gitDetails = new GitDetailsEntity();
            repositoryEntity.gitDetails.ref = dto.gitDetails().ref();
            repositoryEntity.gitDetails.rootPath = dto.gitDetails().rootPath();
        }
        repositoryEntity.persist();

        RepositoryDto result = gitRepositoryMapper.toDto(repositoryEntity);
        return RestResponse.ResponseBuilder
                .<RepositoryDto>create(RestResponse.Status.OK)
                .entity(result)
                .build();
    }

    @GET
    @Path("/{productName}/repositories")
    public RestResponse<List<RepositoryDto>> getRepositories(@PathParam("productName") String productName) {
        return ProductEntity.<ProductEntity>findByIdOptional(productName)
                .map(productEntity -> {
                    return productEntity.repositories.stream()
                            .map(gitRepositoryEntity -> gitRepositoryMapper.toDto(gitRepositoryEntity))
                            .sorted(Comparator.comparing(RepositoryDto::url))
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

}
