package com.localgrower.service.custom;

import com.localgrower.domain.*;
import com.localgrower.repository.ProductRepository;
import com.localgrower.repository.custom.ProductCustomRepository;
import com.localgrower.service.ProductQueryService;
import com.localgrower.service.criteria.ProductCriteria;
import com.localgrower.service.dto.ProductDTO;
import com.localgrower.service.mapper.ProductMapper;
import com.zaxxer.hikari.util.IsolationLevel;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

@Service
@Transactional(readOnly = true)
public class ProductCustomQueryService extends QueryService<Product> {

    private final Logger log = LoggerFactory.getLogger(ProductQueryService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductCustomQueryService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findByIdCategory(ProductCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.findAll(specification, page).map(productMapper::toDto);
    }

    protected Specification<Product> createSpecification(ProductCriteria criteria) {
        Specification<Product> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getIdCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getIdCategoryId(),
                            root -> root.join(Product_.idCategory, JoinType.LEFT).get(Category_.idCategory)
                        )
                    );
            }
        }
        return specification;
    }
}
