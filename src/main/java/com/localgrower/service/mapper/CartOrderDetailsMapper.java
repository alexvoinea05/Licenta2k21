package com.localgrower.service.mapper;

import com.localgrower.domain.*;
import com.localgrower.service.dto.CartItemsDTO;
import com.localgrower.service.dto.CartOrderDetailsDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartOrderDetails} and its DTO {@link CartOrderDetailsDTO}.
 */
@Mapper(componentModel = "spring", uses = { AppUserMapper.class, CartItemsMapper.class })
public interface CartOrderDetailsMapper extends EntityMapper<CartOrderDetailsDTO, CartOrderDetails> {
    //    @Mapping(target = "idAppUser", source = "idAppUser", qualifiedByName = "idAppUser")
    //    @Mapping(target = "cartItems", source = "cartItems", qualifiedByName = "cartItems")
    CartOrderDetailsDTO toDto(CartOrderDetails s);

    @Named(value = "withoutCartItems")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "idAppUser", source = "idAppUser", qualifiedByName = "idAppUser")
    @Mapping(target = "idCartOrderDetails", source = "idCartOrderDetails")
    @Mapping(target = "statusCommand", source = "statusCommand")
    @Mapping(target = "totalPrice", source = "totalPrice")
    CartOrderDetailsDTO toDtoWithoutCartItems(CartOrderDetails s);

    @Named("idCartOrderDetails")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "idCartOrderDetails", source = "idCartOrderDetails")
    //    @Mapping()
    CartOrderDetailsDTO toDtoId(CartOrderDetails cartOrderDetails);
}
