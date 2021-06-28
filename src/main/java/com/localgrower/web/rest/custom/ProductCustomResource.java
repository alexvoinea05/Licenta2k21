package com.localgrower.web.rest.custom;

import com.localgrower.domain.Product;
import com.localgrower.domain.custom.AddressPair;
import com.localgrower.repository.ProductRepository;
import com.localgrower.repository.custom.ProductCustomRepository;
import com.localgrower.service.ProductQueryService;
import com.localgrower.service.ProductService;
import com.localgrower.service.criteria.ProductCriteria;
import com.localgrower.service.custom.ProductCustomQueryService;
import com.localgrower.service.dto.ProductDTO;
import com.localgrower.service.dto.custom.*;
import com.localgrower.web.rest.ProductResource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class ProductCustomResource extends QueryService<Product> {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @GetMapping("/category/products/{id}")
    public ResponseEntity<List<ProductDTO>> getAllCategoryProducts(@PathVariable Long id, ProductCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Products by criteria: {}", criteria);
        log.debug("Id ul este", id.toString());
        LongFilter longFilter = new LongFilter();
        longFilter.setEquals(id);
        criteria.setIdCategoryId(longFilter);
        Page<ProductDTO> page = productQueryService.findByIdCategory(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final ProductCustomQueryService productQueryService;

    public ProductCustomResource(
        ProductService productService,
        ProductRepository productRepository,
        ProductCustomQueryService productQueryService
    ) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productQueryService = productQueryService;
    }

    //    @GetMapping("/category/products/{id}")
    //    public ResponseEntity<List<ProductDTO>> getAllCategoryProducts(@PathVariable Long id, ProductCriteria criteria, Pageable pageable) {
    //        log.debug("REST request to get Products by criteria: {}", criteria);
    //        log.debug("Id ul este", id.toString());
    //        LongFilter longFilter = new LongFilter();
    //        longFilter.setEquals(id);
    //        criteria.setIdCategoryId(longFilter);
    //        Page<ProductDTO> page = productQueryService.findByIdCategory(criteria, pageable);
    //        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    //        return ResponseEntity.ok().headers(headers).body(page.getContent());
    //    }

    @GetMapping("/product/alghorithm")
    public ResponseEntity<List<ProductDTO>> getAllWantedProducts(
        @RequestParam("numeProdus") List<String> numeProdus,
        @RequestParam("cantitate") List<String> cantitate
    ) {
        List<ProductAndPriceDTO> productAndPriceDTOList = new ArrayList<>();
        List<ProductSearchDTO> productSearchDTOList = new ArrayList<>();
        for (int i = 0; i < numeProdus.size(); i++) {
            productSearchDTOList.add(new ProductSearchDTO(numeProdus.get(i), new BigDecimal(cantitate.get(i))));
        }
        if (!productSearchDTOList.isEmpty()) {
            productAndPriceDTOList = productQueryService.findAllWantedProducts(productSearchDTOList).get();
        }
        List<ProductWantedOverall> productWantedOverallList = new ArrayList<>();
        productWantedOverallList = productQueryService.getProductWantedOverallList(productAndPriceDTOList);
        List<ProductOverallAddressPairDistanceTime> productOverallAddressPairDistanceTimeList = new ArrayList<>();
        productOverallAddressPairDistanceTimeList =
            productQueryService.getProductOverallAddressPairDistanceTimeList(productWantedOverallList);

        List<ProductDTO> productDTOListFinal = new ArrayList<>();
        productDTOListFinal = productQueryService.getAllProductsWantedAfterAlgorithm(productOverallAddressPairDistanceTimeList);
        Page<ProductDTO> page = new PageImpl<>(productDTOListFinal);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
