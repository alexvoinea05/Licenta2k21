package com.localgrower.repository.custom;

import com.localgrower.domain.Product;
import com.localgrower.service.dto.ProductDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCustomRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    // @Query("select p from Product p where p.category.idCategory = :categoryId ")
    ///Optional<List<Product>> findAllForCategoryId(@Param("categoryId") Long categoryId);
}
