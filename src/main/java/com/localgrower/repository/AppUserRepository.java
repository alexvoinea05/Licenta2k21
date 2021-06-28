package com.localgrower.repository;

import com.localgrower.domain.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AppUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @Query("select a from AppUser a where a.user.id = :idUser  ")
    Optional<AppUser> findAppUserByUserId(@Param("idUser") Long idUser);
}
