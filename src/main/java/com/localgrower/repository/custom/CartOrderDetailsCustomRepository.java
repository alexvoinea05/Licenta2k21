package com.localgrower.repository.custom;

import com.localgrower.domain.CartOrderDetails;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartOrderDetailsCustomRepository
    extends JpaRepository<CartOrderDetails, Long>, JpaSpecificationExecutor<CartOrderDetails> {
    //(idCartOrderDetails,totalPrice,createdAt,modifiedAt,idAppUser,statusCommand)
    @Modifying
    @Query(
        value = "insert into cart_order_details (total_price,created_at,modified_at,id_app_user_id_app_user,status_command) VALUES (:totalPrice,:created,:modified,:idAppUser,:statusCommand)",
        nativeQuery = true
    )
    @Transactional
    void createCartOrderDetails(
        @Param("totalPrice") BigDecimal totalPrice,
        @Param("created") ZonedDateTime created,
        @Param("modified") ZonedDateTime modified,
        @Param("idAppUser") Long idAppUser,
        @Param("statusCommand") String statusCommand
    );

    @Modifying
    @Query(
        value = "update CartOrderDetails set totalPrice=:totalPrice,modifiedAt=:modified where idCartOrderDetails=:idCartOrderDetails",
        nativeQuery = true
    )
    @Transactional
    void updateCartOrderDetails(
        @Param("idCartOrderDetails") Long idCartOrderDetails,
        @Param("totalPrice") BigDecimal totalPrice,
        @Param("modified") ZonedDateTime modified
    );

    @Modifying
    @Query(
        value = "update CartOrderDetails set statusCommand=:statusCommand,modifiedAt=:modified where idCartOrderDetails=:idCartOrderDetails",
        nativeQuery = true
    )
    @Transactional
    void updateCartOrderDetailsStatus(
        @Param("idCartOrderDetails") Long idCartOrderDetails,
        @Param("statusCommand") String statusCommand,
        @Param("modified") ZonedDateTime modified
    );

    @Query(
        "select c from CartOrderDetails c where c.idAppUser.idAppUser = :idAppUser and (c.statusCommand !=:statusCommand " +
        "and c.statusCommand !=:statusCommand1)"
    )
    Optional<CartOrderDetails> findActiveCartOrderDetailsForCurrentAppUser(
        @Param("idAppUser") Long idAppUser,
        @Param("statusCommand") String statusCommand,
        @Param("statusCommand1") String statusCommand1
    );
}
