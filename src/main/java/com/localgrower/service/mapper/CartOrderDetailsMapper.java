package com.localgrower.service.mapper;

import com.localgrower.domain.*;
import com.localgrower.service.dto.CartOrderDetailsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartOrderDetails} and its DTO {@link CartOrderDetailsDTO}.
 */
@Mapper(componentModel = "spring", uses = { AppUserMapper.class })
public interface CartOrderDetailsMapper extends EntityMapper<CartOrderDetailsDTO, CartOrderDetails> {
    @Mapping(target = "idAppUser", source = "idAppUser", qualifiedByName = "idAppUser")
    CartOrderDetailsDTO toDto(CartOrderDetails s);

    @Named("idCartOrderDetails")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "idCartOrderDetails", source = "idCartOrderDetails")
    CartOrderDetailsDTO toDtoId(CartOrderDetails cartOrderDetails);
}
