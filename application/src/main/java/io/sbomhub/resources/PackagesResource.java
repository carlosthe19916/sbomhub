package io.sbomhub.resources;

import io.sbomhub.dto.PackageWithVersionCountDto;
import io.sbomhub.dto.SearchResultDto;
import io.sbomhub.mapper.ApplicationPackageMapper;
import io.sbomhub.mapper.SearchResultMapper;
import io.sbomhub.models.ApplicationPackageFilterBean;
import io.sbomhub.models.PageBean;
import io.sbomhub.models.SearchResultBean;
import io.sbomhub.models.SortBean;
import io.sbomhub.models.jpa.PackageWithVersionCountProjection;
import io.sbomhub.models.jpa.ApplicationPackageRepository;
import io.sbomhub.models.jpa.entity.SbomEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
@ApplicationScoped
@Path("/packages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PackagesResource {

    @Inject
    ApplicationPackageRepository applicationPackageRepository;

    @Inject
    ApplicationPackageMapper applicationPackageMapper;

    @Inject
    SearchResultMapper searchResultMapper;

    @GET
    @Path("/")
    public SearchResultDto<PackageWithVersionCountDto> listPackages(
            @QueryParam("q") String filterText,
            @QueryParam("sbom") List<Long> sbomId,
            @QueryParam("offset") @DefaultValue("0") @Max(9_000) Integer offset,
            @QueryParam("limit") @DefaultValue("10") @Max(1_000) Integer limit,
            @QueryParam("sort_by") List<String> sortBy
    ) {
        List<SbomEntity> sboms = sbomId != null
                ? sbomId.stream().map(SbomEntity::<SbomEntity>findByIdOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList()
                : Collections.emptyList();

        PageBean pageBean = PageBean.buildWith(offset, limit);
        List<SortBean> sortBeans = SortBean.buildWith(sortBy, ApplicationPackageRepository.SORT_BY_FIELDS);
        ApplicationPackageFilterBean filterBean = new ApplicationPackageFilterBean(filterText, sboms);

        SearchResultBean<PackageWithVersionCountProjection> searchResult = applicationPackageRepository.listNames(filterBean, pageBean, sortBeans);

        return searchResultMapper.toDto(searchResult, projection -> applicationPackageMapper.toDto(projection));
    }

}
