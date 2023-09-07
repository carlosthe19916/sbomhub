package io.sbomhub.resources;

import io.sbomhub.dto.OrganizationDto;
import io.sbomhub.mapper.OrganizationMapper;
import io.sbomhub.models.jpa.entity.OrganizationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
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
                    entity.name = organizationDto.name();
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

}
