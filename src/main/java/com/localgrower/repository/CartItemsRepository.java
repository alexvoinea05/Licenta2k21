package com.localgrower.repository;

import com.localgrower.domain.CartItems;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CartItems entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {}
