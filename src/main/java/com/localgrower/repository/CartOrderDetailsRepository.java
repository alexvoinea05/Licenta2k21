package com.localgrower.repository;

import com.localgrower.domain.CartOrderDetails;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CartOrderDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartOrderDetailsRepository extends JpaRepository<CartOrderDetails, Long> {}
