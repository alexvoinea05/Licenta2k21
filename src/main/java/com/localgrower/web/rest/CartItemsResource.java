package com.localgrower.web.rest;

import com.localgrower.domain.CartItems;
import com.localgrower.domain.CartOrderDetails;
import com.localgrower.domain.User;
import com.localgrower.repository.AppUserRepository;
import com.localgrower.repository.CartItemsRepository;
import com.localgrower.repository.CartOrderDetailsRepository;
import com.localgrower.repository.UserRepository;
import com.localgrower.security.SecurityUtils;
import com.localgrower.service.CartItemsService;
import com.localgrower.service.dto.CartItemsDTO;
import com.localgrower.service.dto.CartOrderDetailsDTO;
import com.localgrower.service.mapper.CartItemsMapper;
import com.localgrower.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
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
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.localgrower.domain.CartItems}.
 */
@RestController
@RequestMapping("/api")
public class CartItemsResource {

    private final Logger log = LoggerFactory.getLogger(CartItemsResource.class);

    private static final String ENTITY_NAME = "cartItems";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartItemsService cartItemsService;

    private final CartItemsRepository cartItemsRepository;

    private final UserRepository userRepository;

    private final AppUserRepository appUserRepository;

    private final CartOrderDetailsRepository cartOrderDetailsRepository;

    private final CartItemsMapper cartItemsMapper;

    public CartItemsResource(
        CartItemsService cartItemsService,
        CartItemsRepository cartItemsRepository,
        UserRepository userRepository,
        AppUserRepository appUserRepository,
        CartOrderDetailsRepository cartOrderDetailsRepository,
        CartItemsMapper cartItemsMapper
    ) {
        this.cartItemsService = cartItemsService;
        this.cartItemsRepository = cartItemsRepository;
        this.userRepository = userRepository;
        this.appUserRepository = appUserRepository;
        this.cartOrderDetailsRepository = cartOrderDetailsRepository;
        this.cartItemsMapper = cartItemsMapper;
    }

    /**
     * {@code POST  /cart-items} : Create a new cartItems.
     *
     * @param cartItemsDTO the cartItemsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartItemsDTO, or with status {@code 400 (Bad Request)} if the cartItems has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cart-items")
    public ResponseEntity<CartItemsDTO> createCartItems(@Valid @RequestBody CartItemsDTO cartItemsDTO) throws URISyntaxException {
        log.debug("REST request to save CartItems : {}", cartItemsDTO);
        if (cartItemsDTO.getIdCartItems() != null) {
            throw new BadRequestAlertException("A new cartItems cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CartItemsDTO result = cartItemsService.save(cartItemsDTO);
        return ResponseEntity
            .created(new URI("/api/cart-items/" + result.getIdCartItems()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getIdCartItems().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cart-items/:idCartItems} : Updates an existing cartItems.
     *
     * @param idCartItems the id of the cartItemsDTO to save.
     * @param cartItemsDTO the cartItemsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemsDTO,
     * or with status {@code 400 (Bad Request)} if the cartItemsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartItemsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    //    @PutMapping("/cart-items/{idCartItems}")
    //    public ResponseEntity<CartItemsDTO> updateCartItems(
    //        @PathVariable(value = "idCartItems", required = false) final Long idCartItems,
    //        @Valid @RequestBody CartItemsDTO cartItemsDTO
    //    ) throws URISyntaxException {
    //        log.debug("REST request to update CartItems : {}, {}", idCartItems, cartItemsDTO);
    //        if (cartItemsDTO.getIdCartItems() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(idCartItems, cartItemsDTO.getIdCartItems())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!cartItemsRepository.existsById(idCartItems)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //
    //        CartItemsDTO result = cartItemsService.save(cartItemsDTO);
    //
    //        return ResponseEntity
    //            .ok()
    //            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartItemsDTO.getIdCartItems().toString()))
    //            .body(result);
    //    }
    //
    //    /**
    //     * {@code PATCH  /cart-items/:idCartItems} : Partial updates given fields of an existing cartItems, field will ignore if it is null
    //     *
    //     * @param idCartItems the id of the cartItemsDTO to save.
    //     * @param cartItemsDTO the cartItemsDTO to update.
    //     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemsDTO,
    //     * or with status {@code 400 (Bad Request)} if the cartItemsDTO is not valid,
    //     * or with status {@code 404 (Not Found)} if the cartItemsDTO is not found,
    //     * or with status {@code 500 (Internal Server Error)} if the cartItemsDTO couldn't be updated.
    //     * @throws URISyntaxException if the Location URI syntax is incorrect.
    //     */
    //    @PatchMapping(value = "/cart-items/{idCartItems}", consumes = "application/merge-patch+json")
    //    public ResponseEntity<CartItemsDTO> partialUpdateCartItems(
    //        @PathVariable(value = "idCartItems", required = false) final Long idCartItems,
    //        @NotNull @RequestBody CartItemsDTO cartItemsDTO
    //    ) throws URISyntaxException {
    //        log.debug("REST request to partial update CartItems partially : {}, {}", idCartItems, cartItemsDTO);
    //        if (cartItemsDTO.getIdCartItems() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(idCartItems, cartItemsDTO.getIdCartItems())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!cartItemsRepository.existsById(idCartItems)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //
    //        Optional<CartItemsDTO> result = cartItemsService.partialUpdate(cartItemsDTO);
    //
    //        return ResponseUtil.wrapOrNotFound(
    //            result,
    //            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartItemsDTO.getIdCartItems().toString())
    //        );
    //    }

    /**
     * {@code GET  /cart-items} : get all the cartItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartItems in body.
     */
    @GetMapping("/cart-items")
    public ResponseEntity<List<CartItemsDTO>> getAllCartItems(Pageable pageable) {
        log.debug("REST request to get a page of CartItems");
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        User user = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).get();
        List<CartItemsDTO> cartItemsDTOList = cartItemsMapper.toDto(cartItemsList);
        List<CartItemsDTO> cartItemsDTOListFinal = new ArrayList<>();
        for (CartItemsDTO cartItemsDTO : cartItemsDTOList) {
            CartOrderDetailsDTO idCartOrderDetails = cartItemsDTO.getIdOrderDetails();
            CartOrderDetails cartOrderDetails = cartOrderDetailsRepository.findById(idCartOrderDetails.getIdCartOrderDetails()).get();
            User userCart = cartOrderDetails.getIdAppUser().getUser();
            log.debug("UserDTO is:{}", userCart);

            Long idCart = userCart.getId();
            log.debug("IDD cart:{}", idCart);
            Long idUser = user.getId();
            log.debug("IDD user curre:{}", idUser);
            if (idCart.equals(idUser)) {
                cartItemsDTOListFinal.add(cartItemsDTO);
            }
        }
        Page<CartItemsDTO> cartItemsDTOPage = new PageImpl<>(cartItemsDTOListFinal);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            cartItemsDTOPage
        );
        return ResponseEntity.ok().headers(headers).body(cartItemsDTOPage.getContent());
    }

    /**
     * {@code GET  /cart-items/:id} : get the "id" cartItems.
     *
     * @param id the id of the cartItemsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartItemsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cart-items/{id}")
    public ResponseEntity<CartItemsDTO> getCartItems(@PathVariable Long id) {
        log.debug("REST request to get CartItems : {}", id);
        Optional<CartItemsDTO> cartItemsDTO = cartItemsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cartItemsDTO);
    }
    /**
     * {@code DELETE  /cart-items/:id} : delete the "id" cartItems.
     *
     * @param id the id of the cartItemsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    //    @DeleteMapping("/cart-items/{id}")
    //    public ResponseEntity<Void> deleteCartItems(@PathVariable Long id) {
    //        log.debug("REST request to delete CartItems : {}", id);
    //        cartItemsService.delete(id);
    //        return ResponseEntity
    //            .noContent()
    //            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
    //            .build();
    //    }
}
