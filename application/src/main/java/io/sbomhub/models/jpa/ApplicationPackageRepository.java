package io.sbomhub.models.jpa;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.sbomhub.models.ApplicationPackageFilterBean;
import io.sbomhub.models.PageBean;
import io.sbomhub.models.SearchResultBean;
import io.sbomhub.models.SortBean;
import io.sbomhub.models.jpa.entity.ApplicationPackageEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@ApplicationScoped
public class ApplicationPackageRepository implements PanacheRepositoryBase<ApplicationPackageEntity, String> {

    public static final String[] SORT_BY_FIELDS = {"name"};

    public SearchResultBean<PackageWithVersionCountProjection> listNames(ApplicationPackageFilterBean filterBean, PageBean pageBean, List<SortBean> sortBy) {
        Sort sort = Sort.by();
        sortBy.forEach(f -> sort.and(f.fieldName(), f.asc() ? Sort.Direction.Ascending : Sort.Direction.Descending));

//        SELECT last_name, count(distinct first_name) FROM patients group by last_name order by last_name;
        StringBuilder queryBuilder = new StringBuilder("select p.name as name, count(distinct p.version) as version_count from ApplicationPackageEntity p ");
        StringBuilder totalQueryBuilder = new StringBuilder("select count(distinct p.name) as count from ApplicationPackageEntity p ");

        Parameters parameters = new Parameters();
        List<String> queryConditions = new ArrayList<>();

        if (filterBean.sbom() != null) {
            queryConditions.add("p.sbom = :sbom");
            parameters.and("sbom", filterBean.sbom());
        }

        if (filterBean.filterText() != null && !filterBean.filterText().isBlank()) {
            queryConditions.add("lower(p.name) like :filterText");
            parameters.and("filterText", "%" + filterBean.filterText().toLowerCase() + "%");
        }

        if (!queryConditions.isEmpty()) {
            queryBuilder
                    .append("where ")
                    .append(String.join(" and ", queryConditions));
        }
        if (!queryConditions.isEmpty()) {
            totalQueryBuilder
                    .append("where ")
                    .append(String.join(" and ", queryConditions));
        }

        queryBuilder.append(" group by p.name");

        PanacheQuery<PackageWithVersionCountProjection> query = ApplicationPackageEntity
                .find(queryBuilder.toString(), sort, parameters)
                .range(pageBean.offset(), pageBean.offset() + pageBean.limit() - 1)
                .project(PackageWithVersionCountProjection.class);

        TotalProjection totalProjection = ApplicationPackageEntity.find(totalQueryBuilder.toString(), parameters)
                .project(TotalProjection.class)
                .singleResult();

        return new SearchResultBean<>(pageBean.offset(), pageBean.limit(), totalProjection.total(), query.list());
    }

//    public SearchResultBean<ApplicationPackageEntity> list(ApplicationPackageFilterBean filterBean, PageBean pageBean, List<SortBean> sortBy) {
//        Sort sort = Sort.by();
//        sortBy.forEach(f -> sort.and(f.fieldName(), f.asc() ? Sort.Direction.Ascending : Sort.Direction.Descending));
//
//        StringBuilder queryBuilder = new StringBuilder("FROM ApplicationPackage as p WHERE ");
//        Parameters parameters = new Parameters();
//
//        List<String> queryConditions = new ArrayList<>();
//
//        if (filterBean.sboms() != null) {
//            queryConditions.add("p.sboms IN :sboms");
//            parameters.and("sboms", filterBean.sboms());
//        }
//
//        if (filterBean.filterText() != null && !filterBean.filterText().isBlank()) {
//            queryConditions.add("lower(p.id.name) like :filterText");
//            parameters.and("filterText", "%" + filterBean.filterText().toLowerCase() + "%");
//        }
//
//        queryBuilder.append(String.join(" and ", queryConditions));
//
//        PanacheQuery<ApplicationPackageEntity> query = ApplicationPackageEntity
//                .find(queryBuilder.toString(), sort, parameters)
//                .range(pageBean.offset(), pageBean.offset() + pageBean.limit() - 1);
//
//        return new SearchResultBean<ApplicationPackageEntity>(pageBean.offset(), pageBean.limit(), query.count(), query.list());
//    }
}
