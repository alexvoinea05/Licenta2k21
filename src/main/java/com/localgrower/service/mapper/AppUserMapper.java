package com.localgrower.service.mapper;

import com.localgrower.domain.*;
import com.localgrower.service.dto.AppUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUser} and its DTO {@link AppUserDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface AppUserMapper extends EntityMapper<AppUserDTO, AppUser> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    AppUserDTO toDto(AppUser s);

    @Named("idAppUser")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "idAppUser", source = "idAppUser")
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    AppUserDTO toDtoId(AppUser appUser);
}
