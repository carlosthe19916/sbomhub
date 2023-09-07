package io.sbomhub.resources;

import io.sbomhub.dto.PackageWithVersionCountDto;
import io.sbomhub.mapper.ApplicationPackageMapper;
import io.sbomhub.models.ApplicationPackageFilterBean;
import io.sbomhub.models.PageBean;
import io.sbomhub.models.SearchResultBean;
import io.sbomhub.models.SortBean;
import io.sbomhub.models.jpa.ApplicationPackageRepository;
import io.sbomhub.models.jpa.PackageWithVersionCountProjection;
import io.sbomhub.models.jpa.entity.SbomEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

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

    @GET
    @Path("/")
    public SearchResultBean<PackageWithVersionCountDto> listPackages(
            @QueryParam("q") String filterText,
            @QueryParam("sbom") Long sbomId,
            @QueryParam("offset") @DefaultValue("0") @Max(9_000) Integer offset,
            @QueryParam("limit") @DefaultValue("10") @Max(1_000) Integer limit,
            @QueryParam("sort_by") List<String> sortBy
    ) {
        SbomEntity sbomEntity = sbomId != null ? SbomEntity.findById(sbomId) : null;

        PageBean pageBean = PageBean.buildWith(offset, limit);
        List<SortBean> sortBeans = SortBean.buildWith(sortBy, ApplicationPackageRepository.SORT_BY_FIELDS);
        ApplicationPackageFilterBean filterBean = new ApplicationPackageFilterBean(filterText, sbomEntity);

        SearchResultBean<PackageWithVersionCountProjection> searchResult = applicationPackageRepository.listNames(filterBean, pageBean, sortBeans);
        return SearchResultBean.transformData(searchResult, entity -> applicationPackageMapper.toDto(entity));
    }

}
