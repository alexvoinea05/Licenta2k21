package com.localgrower.service.custom;

import com.localgrower.domain.*;
import com.localgrower.repository.AppUserRepository;
import com.localgrower.repository.ProductRepository;
import com.localgrower.repository.UserRepository;
import com.localgrower.repository.custom.CartCustomRepository;
import com.localgrower.repository.custom.CartOrderDetailsCustomRepository;
import com.localgrower.security.SecurityUtils;
import com.localgrower.service.dto.custom.ProductCartCustomDTO;
import com.localgrower.service.mapper.CartItemsMapper;
import com.localgrower.service.mapper.ProductMapper;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import liquibase.pro.packaged.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartCustomService {

    private final Logger log = LoggerFactory.getLogger(CartCustomService.class);

    @Autowired
    private final CartCustomRepository cartCustomRepository;

    @Autowired
    private final CartOrderDetailsCustomRepository cartOrderDetailsCustomRepository;

    @Autowired
    private final CartItemsMapper cartItemsMapper;

    @Autowired
    private final ProductMapper productMapper;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final AppUserRepository appUserRepository;

    @Autowired
    private final ProductRepository productRepository;

    public CartCustomService(
        AppUserRepository appUserRepository,
        UserRepository userRepository,
        ProductMapper productMapper,
        CartCustomRepository cartCustomRepository,
        CartItemsMapper cartItemsMapper,
        CartOrderDetailsCustomRepository cartOrderDetailsCustomRepository,
        ProductRepository productRepository
    ) {
        this.cartCustomRepository = cartCustomRepository;
        this.cartItemsMapper = cartItemsMapper;
        this.cartOrderDetailsCustomRepository = cartOrderDetailsCustomRepository;
        this.productMapper = productMapper;
        this.userRepository = userRepository;
        this.appUserRepository = appUserRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addProduct(ProductCartCustomDTO productCartCustomDTO) {
        log.debug("Cart Intems ok");
        //CartOrderDetailsDTO cartOrderDetailsDTO = cartItemsDTO.getIdOrderDetails();
        if (SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).isPresent()) {
            User user = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).get();
            if (appUserRepository.findAppUserByUserId(user.getId()).isPresent()) {
                AppUser appUser = appUserRepository.findAppUserByUserId(user.getId()).get();
                log.debug("App user has been retrieved", appUser.toString());
                if (
                    cartOrderDetailsCustomRepository
                        .findActiveCartOrderDetailsForCurrentAppUser(appUser.getIdAppUser(), "FINALIZAT")
                        .isPresent()
                ) {
                    CartOrderDetails cartOrderDetails = cartOrderDetailsCustomRepository
                        .findActiveCartOrderDetailsForCurrentAppUser(appUser.getIdAppUser(), "FINALIZAT")
                        .get();
                    log.debug("Cart Order Details has been retrieved", cartOrderDetails);
                    BigDecimal quantity = productCartCustomDTO.getQuantity();
                    if (productRepository.findById(productCartCustomDTO.getIdProduct()).isPresent()) {
                        Product product = productRepository.findById(productCartCustomDTO.getIdProduct()).get();
                        BigDecimal price = product.getPrice();
                        BigDecimal totalPrice = cartOrderDetails.getTotalPrice().add(quantity.multiply(price));
                        cartOrderDetails.setTotalPrice(totalPrice);
                    }
                    //cartOrderDetailsCustomRepository.updateCartOrderDetails(cartOrderDetails.getIdCartOrderDetails(), totalPrice, modified);
                    cartOrderDetails.setModifiedAt(ZonedDateTime.now());
                    cartOrderDetailsCustomRepository.saveAndFlush(cartOrderDetails);

                    if (
                        cartCustomRepository
                            .findProductInCart(productCartCustomDTO.getIdProduct(), cartOrderDetails.getIdCartOrderDetails())
                            .isPresent()
                    ) {
                        log.debug("This object is already in the cart");
                        CartItems cartItems = cartCustomRepository
                            .findProductInCart(productCartCustomDTO.getIdProduct(), cartOrderDetails.getIdCartOrderDetails())
                            .get();
                        BigDecimal totalQuantity = cartItems.getQuantity().add(quantity);
                        cartItems.setQuantity(totalQuantity);
                        cartItems.setModifiedAt(ZonedDateTime.now());
                        cartCustomRepository.save(cartItems);
                    } else {
                        log.debug("This object is not in the cart");
                        CartItems cartItems = new CartItems();
                        cartItems.setQuantity(quantity);
                        if (productRepository.findById(productCartCustomDTO.getIdProduct()).isPresent()) {
                            cartItems.setIdProduct(productRepository.findById(productCartCustomDTO.getIdProduct()).get());
                        }

                        cartItems.setCreatedAt(ZonedDateTime.now());
                        cartItems.setModifiedAt(ZonedDateTime.now());
                        cartItems.setIdOrderDetails(cartOrderDetails);
                        cartCustomRepository.save(cartItems);
                        //cartCustomRepository.addProdToCart(cartItems.getIdCartItems(), productCartCustomDTO.getQuantityProduct(), ZonedDateTime.now(),
                        //  ZonedDateTime.now(), productCartCustomDTO.getIdProduct(), cartOrderDetails.getIdCartOrderDetails());
                    }
                } else {
                    CartOrderDetails cartOrderDetails = new CartOrderDetails();
                    BigDecimal quantity = productCartCustomDTO.getQuantity();
                    if (productRepository.findById(productCartCustomDTO.getIdProduct()).isPresent()) {
                        Product product = productRepository.findById(productCartCustomDTO.getIdProduct()).get();
                        BigDecimal price = product.getPrice();
                        BigDecimal totalPrice = quantity.multiply(price);
                        cartOrderDetails.setTotalPrice(totalPrice);
                    }
                    cartOrderDetails.setIdAppUser(appUser);
                    cartOrderDetails.setCreatedAt(ZonedDateTime.now());
                    cartOrderDetails.setModifiedAt(ZonedDateTime.now());
                    cartOrderDetails.setStatusCommand("NEFINALIZAT");
                    //cartOrderDetailsCustomRepository.createCartOrderDetails( totalPrice, ZonedDateTime.now(), ZonedDateTime.now(), appUser.getIdAppUser(), "NEFINALIZAT");
                    cartOrderDetailsCustomRepository.saveAndFlush(cartOrderDetails);

                    CartItems cartItems = new CartItems();
                    cartItems.setQuantity(quantity);
                    if (productRepository.findById(productCartCustomDTO.getIdProduct()).isPresent()) {
                        cartItems.setIdProduct(productRepository.findById(productCartCustomDTO.getIdProduct()).get());
                    }

                    cartItems.setCreatedAt(ZonedDateTime.now());
                    cartItems.setModifiedAt(ZonedDateTime.now());
                    cartItems.setIdOrderDetails(cartOrderDetails);
                    //cartItems.setIdCartItems(cartOrderDetails.getIdCartOrderDetails());
                    cartCustomRepository.save(cartItems);
                    //cartCustomRepository.addProdToCart(cartItems.getIdCartItems(),quantity,ZonedDateTime.now(),ZonedDateTime.now(),
                    //  productCartCustomDTO.getIdProduct(),cartOrderDetails.getIdCartOrderDetails());
                }
            }
        }
    }
}
