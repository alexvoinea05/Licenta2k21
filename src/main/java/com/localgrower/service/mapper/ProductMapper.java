package com.localgrower.service.mapper;

import com.localgrower.domain.*;
import com.localgrower.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { CategoryMapper.class, AppUserMapper.class })
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "idCategory", source = "idCategory", qualifiedByName = "idCategory")
    @Mapping(target = "idGrower", source = "idGrower", qualifiedByName = "idAppUser")
    ProductDTO toDto(Product s);

    @Named("idProduct")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "idProduct", source = "idProduct")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "idGrower", source = "idGrower", qualifiedByName = "idAppUser")
    ProductDTO toDtoId(Product product);
}
