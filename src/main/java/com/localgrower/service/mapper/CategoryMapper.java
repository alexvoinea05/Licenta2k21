package com.localgrower.service.mapper;

import com.localgrower.domain.*;
import com.localgrower.service.dto.CategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
    @Named("idCategory")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "idCategory", source = "idCategory")
    CategoryDTO toDtoId(Category category);
}
