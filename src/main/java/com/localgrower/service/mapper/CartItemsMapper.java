package com.localgrower.service.mapper;

import com.localgrower.domain.*;
import com.localgrower.service.dto.CartItemsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartItems} and its DTO {@link CartItemsDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, CartOrderDetailsMapper.class })
public interface CartItemsMapper extends EntityMapper<CartItemsDTO, CartItems> {
    @Mapping(target = "idProduct", source = "idProduct", qualifiedByName = "idProduct")
    @Mapping(target = "idOrderDetails", source = "idOrderDetails", qualifiedByName = "idCartOrderDetails")
    CartItemsDTO toDto(CartItems s);
}
