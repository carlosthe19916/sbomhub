package io.sbomhub.models.jpa;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sbomhub.models.PageBean;
import io.sbomhub.models.SearchResultBean;
import io.sbomhub.models.SortBean;
import io.sbomhub.models.jpa.entity.OrganizationEntity;
import io.sbomhub.models.jpa.entity.RepositoryEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@ApplicationScoped
public class RepositoryRepository implements PanacheRepositoryBase<RepositoryEntity, Long> {

    public static final String[] SORT_BY_FIELDS = {"name"};

    @RegisterForReflection
    public record FilterBean(Optional<String> q, List<OrganizationEntity> organizations) {
    }

    public SearchResultBean<RepositoryEntity> list(FilterBean filterBean, PageBean paginationBean, List<SortBean> sortBean) {
        Sort sort = Sort.by();
        sortBean.forEach(f -> sort.and(f.fieldName(), f.asc() ? Sort.Direction.Ascending : Sort.Direction.Descending));

        StringBuilder queryBuilder = new StringBuilder("select r from RepositoryEntity r ");

        Parameters parameters = new Parameters();
        List<String> queryConditions = new ArrayList<>();

        if (filterBean.organizations != null && !filterBean.organizations.isEmpty()) {
            queryConditions.add("r.organization in :organizations");
            parameters.and("organizations", filterBean.organizations);
        }
        if (filterBean.q().isPresent()) {
            queryConditions.add("lower(r.name) like :q");
            parameters.and("q", filterBean.q().map(s -> s.replaceAll("\\*", "%").toLowerCase()).get());
        }

        if (!queryConditions.isEmpty()) {
            queryBuilder
                    .append("where ")
                    .append(String.join(" and ", queryConditions));
        }

        PanacheQuery<RepositoryEntity> query = RepositoryEntity
                .find(queryBuilder.toString(), sort, parameters)
                .range(paginationBean.offset(), paginationBean.offset() + paginationBean.limit() - 1);

        return new SearchResultBean<>(query.list(), query.count());
    }

}
