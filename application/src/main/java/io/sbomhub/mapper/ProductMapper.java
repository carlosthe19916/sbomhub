package io.sbomhub.mapper;

import io.sbomhub.dto.ProductDto;
import io.sbomhub.models.jpa.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ProductMapper {

    ProductDto toDto(ProductEntity entity);

}
