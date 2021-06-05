package com.localgrower.repository.custom;

import com.localgrower.domain.CartItems;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface CartCustomRepository extends JpaRepository<CartItems, Long> {
    //    @Modifying
    //    @Query(value = "insert into CartItems (idCartItems,quantity,createdAt,modifiedAt,idProduct,idOrderDetails) VALUES (:idCart,:quantity,:created,:modified,:idProd,:idOrder)", nativeQuery = true)
    //    @Transactional
    //    void addProdToCart(@Param("idCart") Long idCart, @Param("quantity") BigDecimal quantity, @Param("created")ZonedDateTime created, @Param("modified") ZonedDateTime modified, @Param("idProd") Long idProd, @Param("idOrder") Long idOrder);

    @Query(
        value = "select c from CartItems c where c.idProduct.idProduct=:idProduct and c.idOrderDetails.idCartOrderDetails=:idCartOrderDetails"
    )
    Optional<CartItems> findProductInCart(@Param("idProduct") Long idProduct, @Param("idCartOrderDetails") Long idCartOrderDetails);
}
