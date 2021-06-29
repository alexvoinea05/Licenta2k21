package com.localgrower.web.rest.custom;

import com.localgrower.domain.CartItems;
import com.localgrower.domain.CartOrderDetails;
import com.localgrower.domain.Product;
import com.localgrower.repository.CartItemsRepository;
import com.localgrower.repository.CartOrderDetailsRepository;
import com.localgrower.repository.ProductRepository;
import com.localgrower.service.CartItemsService;
import com.localgrower.service.CartOrderDetailsService;
import com.localgrower.service.custom.CartCustomService;
import com.localgrower.service.dto.CartItemsDTO;
import com.localgrower.service.dto.custom.ProductCartCustomDTO;
import com.localgrower.service.mapper.CartOrderDetailsMapper;
import com.localgrower.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api")
public class CartCustomResource {

    @PostMapping("/add/product")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToCart(@Valid @RequestBody ProductCartCustomDTO productCartCustomDTO) throws Exception {
        if(productRepository.findById(productCartCustomDTO.getIdProduct()).get().getStock().compareTo(productCartCustomDTO.getQuantity())<0){
                throw new Exception("Cantitatea selectata este mai mare decat stocul!");
        }
        cartCustomService.addProduct(productCartCustomDTO);
    }

    private final Logger log = LoggerFactory.getLogger(CartCustomResource.class);

    private static final String ENTITY_NAME = "cartItems";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartCustomService cartCustomService;

    private final CartItemsService cartItemsService;

    private final CartItemsRepository cartItemsRepository;

    private final CartOrderDetailsRepository cartOrderDetailsRepository;

    private final ProductRepository productRepository;

    private final CartOrderDetailsService cartOrderDetailsService;

    private final CartOrderDetailsMapper cartOrderDetailsMapper;

    public CartCustomResource(
        CartCustomService cartCustomService,
        CartItemsService cartItemsService,
        CartItemsRepository cartItemsRepository,
        CartOrderDetailsRepository cartOrderDetailsRepository,
        ProductRepository productRepository,
        CartOrderDetailsService cartOrderDetailsService,
        CartOrderDetailsMapper cartOrderDetailsMapper
    ) {
        this.cartCustomService = cartCustomService;
        this.cartItemsService = cartItemsService;
        this.cartItemsRepository = cartItemsRepository;
        this.cartOrderDetailsRepository = cartOrderDetailsRepository;
        this.productRepository = productRepository;
        this.cartOrderDetailsService = cartOrderDetailsService;
        this.cartOrderDetailsMapper = cartOrderDetailsMapper;
    }

    //    @PostMapping("/add/product")
    //    @ResponseStatus(HttpStatus.CREATED)
    //    public void addProductToCart(@Valid @RequestBody ProductCartCustomDTO productCartCustomDTO) {
    //        cartCustomService.addProduct(productCartCustomDTO);
    //    }

    @PutMapping("/custom/cart-items/{idCartItems}")
    public ResponseEntity<CartItemsDTO> updateCartItems(
        @PathVariable(value = "idCartItems", required = false) final Long idCartItems,
        @Valid @RequestBody CartItemsDTO cartItemsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CartItems : {}, {}", idCartItems, cartItemsDTO);
        if (cartItemsDTO.getIdCartItems() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idCartItems, cartItemsDTO.getIdCartItems())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemsRepository.existsById(idCartItems)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BigDecimal quntityBeforeUpdate = cartItemsRepository.findById(idCartItems).get().getQuantity();

        CartItemsDTO result = cartItemsService.save(cartItemsDTO);

        if (cartOrderDetailsRepository.findById(cartItemsDTO.getIdOrderDetails().getIdCartOrderDetails()).isPresent()) {
            CartOrderDetails cartOrderDetails = cartOrderDetailsRepository
                .findById(cartItemsDTO.getIdOrderDetails().getIdCartOrderDetails())
                .get();
            if (productRepository.findById(cartItemsDTO.getIdProduct().getIdProduct()).isPresent()) {
                Product product = productRepository.findById(cartItemsDTO.getIdProduct().getIdProduct()).get();
                BigDecimal price = product.getPrice();
                BigDecimal totalPrice = cartOrderDetails
                    .getTotalPrice()
                    .subtract(quntityBeforeUpdate.multiply(price))
                    .add(price.multiply(cartItemsDTO.getQuantity()));
                cartOrderDetails.setTotalPrice(totalPrice);
                cartOrderDetails.setModifiedAt(ZonedDateTime.now());

                cartOrderDetailsService.save(cartOrderDetailsMapper.toDto(cartOrderDetails));
            }
        }

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartItemsDTO.getIdCartItems().toString()))
            .body(result);
    }

    @DeleteMapping("/custom/cart-items/{id}")
    public ResponseEntity<Void> deleteCartItems(@PathVariable Long id) {
        log.debug("REST request to delete CartItems : {}", id);
        if (cartItemsRepository.findById(id).isPresent()) {
            CartItems cartItems = cartItemsRepository.findById(id).get();
            if (cartOrderDetailsRepository.findById(cartItems.getIdOrderDetails().getIdCartOrderDetails()).isPresent()) {
                CartOrderDetails cartOrderDetails = cartOrderDetailsRepository
                    .findById(cartItems.getIdOrderDetails().getIdCartOrderDetails())
                    .get();
                Product product = productRepository.findById(cartItems.getIdProduct().getIdProduct()).get();
                BigDecimal totalPrice = cartOrderDetails.getTotalPrice().subtract(product.getPrice().multiply(cartItems.getQuantity()));

                if (totalPrice.compareTo(BigDecimal.valueOf(0)) == 1) {
                    cartOrderDetails.setTotalPrice(totalPrice);

                    cartOrderDetailsService.save(cartOrderDetailsMapper.toDto(cartOrderDetails));
                    cartItemsService.delete(id);
                } else {
                    cartItemsService.delete(id);
                    cartOrderDetailsService.delete(cartItems.getIdOrderDetails().getIdCartOrderDetails());
                }
            }
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @PatchMapping(value = "/custom/cart-items/{idCartItems}", consumes = "application/merge-patch+json")
    public ResponseEntity<CartItemsDTO> partialUpdateCartItems(
        @PathVariable(value = "idCartItems", required = false) final Long idCartItems,
        @NotNull @RequestBody CartItemsDTO cartItemsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CartItems partially : {}, {}", idCartItems, cartItemsDTO);
        if (cartItemsDTO.getIdCartItems() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idCartItems, cartItemsDTO.getIdCartItems())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemsRepository.existsById(idCartItems)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BigDecimal quntityBeforeUpdate = cartItemsRepository.findById(idCartItems).get().getQuantity();

        Optional<CartItemsDTO> result = cartItemsService.partialUpdate(cartItemsDTO);

        if (cartOrderDetailsRepository.findById(cartItemsDTO.getIdOrderDetails().getIdCartOrderDetails()).isPresent()) {
            CartOrderDetails cartOrderDetails = cartOrderDetailsRepository
                .findById(cartItemsDTO.getIdOrderDetails().getIdCartOrderDetails())
                .get();
            if (productRepository.findById(cartItemsDTO.getIdProduct().getIdProduct()).isPresent()) {
                Product product = productRepository.findById(cartItemsDTO.getIdProduct().getIdProduct()).get();
                BigDecimal price = product.getPrice();
                BigDecimal totalPrice = cartOrderDetails
                    .getTotalPrice()
                    .subtract(quntityBeforeUpdate.multiply(price))
                    .add(price.multiply(cartItemsDTO.getQuantity()));
                cartOrderDetails.setTotalPrice(totalPrice);
                cartOrderDetails.setModifiedAt(ZonedDateTime.now());

                cartOrderDetailsService.save(cartOrderDetailsMapper.toDto(cartOrderDetails));
            }
        }
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartItemsDTO.getIdCartItems().toString())
        );
    }
}
