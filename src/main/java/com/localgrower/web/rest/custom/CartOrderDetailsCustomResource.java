package com.localgrower.web.rest.custom;

import com.localgrower.domain.CartItems;
import com.localgrower.domain.CartOrderDetails;
import com.localgrower.domain.Product;
import com.localgrower.repository.CartItemsRepository;
import com.localgrower.repository.CartOrderDetailsRepository;
import com.localgrower.repository.ProductRepository;
import com.localgrower.repository.custom.CartCustomRepository;
import com.localgrower.service.CartOrderDetailsService;
import com.localgrower.service.ProductService;
import com.localgrower.service.dto.CartOrderDetailsDTO;
import com.localgrower.service.dto.ProductDTO;
import com.localgrower.service.mapper.CartOrderDetailsMapper;
import com.localgrower.service.mapper.ProductMapper;
import com.localgrower.web.rest.errors.BadRequestAlertException;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api")
public class CartOrderDetailsCustomResource {

    private final Logger log = LoggerFactory.getLogger(CartOrderDetailsCustomResource.class);

    private static final String ENTITY_NAME = "cartOrderDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartOrderDetailsRepository cartOrderDetailsRepository;

    private final CartOrderDetailsService cartOrderDetailsService;

    private final CartOrderDetailsMapper cartOrderDetailsMapper;

    private final ProductRepository productRepository;

    private final ProductService productService;

    private final ProductMapper productMapper;

    private final CartCustomRepository cartCustomRepository;

    public CartOrderDetailsCustomResource(
        CartOrderDetailsRepository cartOrderDetailsRepository,
        CartOrderDetailsService cartOrderDetailsService,
        CartOrderDetailsMapper cartOrderDetailsMapper,
        ProductRepository productRepository,
        ProductService productService,
        ProductMapper productMapper,
        CartCustomRepository cartCustomRepository,
        CartItemsRepository cartItemsRepository,
        CartCustomRepository cartCustomRepository1
    ) {
        this.cartOrderDetailsRepository = cartOrderDetailsRepository;
        this.cartOrderDetailsService = cartOrderDetailsService;
        this.cartOrderDetailsMapper = cartOrderDetailsMapper;
        this.productRepository = productRepository;
        this.productService = productService;
        this.productMapper = productMapper;
        this.cartCustomRepository = cartCustomRepository1;
    }

    @PutMapping("/custom/cart-order-details/{idCartOrderDetails}")
    public ResponseEntity<CartOrderDetailsDTO> updateCartItems(@PathVariable(value = "idCartOrderDetails") final Long idCartOrderDetails)
        throws URISyntaxException {
        log.debug("REST request to update CartItems : {}, {}", idCartOrderDetails);

        if (!cartOrderDetailsRepository.existsById(idCartOrderDetails)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        //CartOrderDetailsDTO result = cartOrderDetailsService.save(cartOrderDetailsMapper.toDto(cartOrderDetailsRepository.findById(idCartOrderDetails).get()));

        CartOrderDetails cartOrderDetails = cartOrderDetailsRepository.findById(idCartOrderDetails).get();
        List<CartItems> cartItemsList = cartCustomRepository.findProductsByIdOrder(cartOrderDetails.getIdCartOrderDetails()).get();
        boolean ok = true;
        for (CartItems cartItems : cartItemsList) {
            if (cartItems.getQuantity().compareTo(cartItems.getIdProduct().getStock()) <= 0) {} else {
                ok = false;
                break;
            }
        }
        if (ok) {
            for (CartItems cartItems : cartItemsList) {
                Product product = productRepository.findById(cartItems.getIdProduct().getIdProduct()).get();
                product.setStock(product.getStock().subtract(cartItems.getQuantity()));
                ProductDTO productDTO = productService.save(productMapper.toDto(product));
            }
            cartOrderDetails.setStatusCommand("FINALIZAT");
        } else {
            cartOrderDetails.setStatusCommand("PRODUSE INDISPONIBILE");
        }
        log.debug("CArt order det:{}", cartOrderDetails);
        CartOrderDetailsDTO result = cartOrderDetailsService.save(cartOrderDetailsMapper.toDtoWithoutCartItems(cartOrderDetails));

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, idCartOrderDetails.toString()))
            .body(result);
    }
}
