package com.localgrower.web.rest;

import com.localgrower.domain.AppUser;
import com.localgrower.domain.CartItems;
import com.localgrower.domain.CartOrderDetails;
import com.localgrower.domain.User;
import com.localgrower.repository.AppUserRepository;
import com.localgrower.repository.CartOrderDetailsRepository;
import com.localgrower.repository.UserRepository;
import com.localgrower.repository.custom.CartCustomRepository;
import com.localgrower.security.SecurityUtils;
import com.localgrower.service.CartOrderDetailsService;
import com.localgrower.service.dto.AppUserDTO;
import com.localgrower.service.dto.CartOrderDetailsDTO;
import com.localgrower.service.mapper.CartOrderDetailsMapper;
import com.localgrower.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.localgrower.domain.CartOrderDetails}.
 */
@RestController
@RequestMapping("/api")
public class CartOrderDetailsResource {

    private final Logger log = LoggerFactory.getLogger(CartOrderDetailsResource.class);

    private static final String ENTITY_NAME = "cartOrderDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartOrderDetailsService cartOrderDetailsService;

    private final CartOrderDetailsRepository cartOrderDetailsRepository;

    private final UserRepository userRepository;

    private final AppUserRepository appUserRepository;

    private final CartCustomRepository cartItemsRepository;

    private final CartOrderDetailsMapper cartOrderDetailsMapper;

    public CartOrderDetailsResource(
        CartOrderDetailsService cartOrderDetailsService,
        CartOrderDetailsRepository cartOrderDetailsRepository,
        UserRepository userRepository,
        AppUserRepository appUserRepository,
        CartCustomRepository cartItemsRepository,
        CartOrderDetailsMapper cartOrderDetailsMapper
    ) {
        this.cartOrderDetailsService = cartOrderDetailsService;
        this.cartOrderDetailsRepository = cartOrderDetailsRepository;
        this.userRepository = userRepository;
        this.appUserRepository = appUserRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.cartOrderDetailsMapper = cartOrderDetailsMapper;
    }

    /**
     * {@code POST  /cart-order-details} : Create a new cartOrderDetails.
     *
     * @param cartOrderDetailsDTO the cartOrderDetailsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartOrderDetailsDTO, or with status {@code 400 (Bad Request)} if the cartOrderDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cart-order-details")
    public ResponseEntity<CartOrderDetailsDTO> createCartOrderDetails(@Valid @RequestBody CartOrderDetailsDTO cartOrderDetailsDTO)
        throws URISyntaxException {
        log.debug("REST request to save CartOrderDetails : {}", cartOrderDetailsDTO);
        if (cartOrderDetailsDTO.getIdCartOrderDetails() != null) {
            throw new BadRequestAlertException("A new cartOrderDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CartOrderDetailsDTO result = cartOrderDetailsService.save(cartOrderDetailsDTO);
        return ResponseEntity
            .created(new URI("/api/cart-order-details/" + result.getIdCartOrderDetails()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getIdCartOrderDetails().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cart-order-details/:idCartOrderDetails} : Updates an existing cartOrderDetails.
     *
     * @param idCartOrderDetails the id of the cartOrderDetailsDTO to save.
     * @param cartOrderDetailsDTO the cartOrderDetailsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartOrderDetailsDTO,
     * or with status {@code 400 (Bad Request)} if the cartOrderDetailsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartOrderDetailsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cart-order-details/{idCartOrderDetails}")
    public ResponseEntity<CartOrderDetailsDTO> updateCartOrderDetails(
        @PathVariable(value = "idCartOrderDetails", required = false) final Long idCartOrderDetails,
        @Valid @RequestBody CartOrderDetailsDTO cartOrderDetailsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CartOrderDetails : {}, {}", idCartOrderDetails, cartOrderDetailsDTO);
        if (cartOrderDetailsDTO.getIdCartOrderDetails() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idCartOrderDetails, cartOrderDetailsDTO.getIdCartOrderDetails())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartOrderDetailsRepository.existsById(idCartOrderDetails)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CartOrderDetailsDTO result = cartOrderDetailsService.save(cartOrderDetailsDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    cartOrderDetailsDTO.getIdCartOrderDetails().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PATCH  /cart-order-details/:idCartOrderDetails} : Partial updates given fields of an existing cartOrderDetails, field will ignore if it is null
     *
     * @param idCartOrderDetails the id of the cartOrderDetailsDTO to save.
     * @param cartOrderDetailsDTO the cartOrderDetailsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartOrderDetailsDTO,
     * or with status {@code 400 (Bad Request)} if the cartOrderDetailsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cartOrderDetailsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cartOrderDetailsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cart-order-details/{idCartOrderDetails}", consumes = "application/merge-patch+json")
    public ResponseEntity<CartOrderDetailsDTO> partialUpdateCartOrderDetails(
        @PathVariable(value = "idCartOrderDetails", required = false) final Long idCartOrderDetails,
        @NotNull @RequestBody CartOrderDetailsDTO cartOrderDetailsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CartOrderDetails partially : {}, {}", idCartOrderDetails, cartOrderDetailsDTO);
        if (cartOrderDetailsDTO.getIdCartOrderDetails() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idCartOrderDetails, cartOrderDetailsDTO.getIdCartOrderDetails())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartOrderDetailsRepository.existsById(idCartOrderDetails)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CartOrderDetailsDTO> result = cartOrderDetailsService.partialUpdate(cartOrderDetailsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartOrderDetailsDTO.getIdCartOrderDetails().toString())
        );
    }

    /**
     * {@code GET  /cart-order-details} : get all the cartOrderDetails.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartOrderDetails in body.
     */
    @GetMapping("/cart-order-details")
    public ResponseEntity<List<CartOrderDetailsDTO>> getAllCartOrderDetails(Pageable pageable) {
        log.debug("REST request to get a page of CartOrderDetails");
        List<CartOrderDetails> cartOrderDetailsList = cartOrderDetailsRepository.findAll();
        User user = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).get();
        List<CartOrderDetailsDTO> cartOrderDetailsDTOList = new ArrayList<>();
        for (CartOrderDetails cartOrderDetails : cartOrderDetailsList) {
            cartOrderDetailsDTOList.add(cartOrderDetailsMapper.toDtoWithoutCartItems(cartOrderDetails));
        }
        List<CartOrderDetailsDTO> cartOrderDetailsDTOFinal = new ArrayList<>();
        for (CartOrderDetailsDTO cartOrderDetailsDTO : cartOrderDetailsDTOList) {
            AppUserDTO idAppUser = cartOrderDetailsDTO.getIdAppUser();
            AppUser appUser = appUserRepository.findById(idAppUser.getIdAppUser()).get();
            User userCartOrderDetails = appUser.getUser();
            if (userCartOrderDetails.getId().equals(user.getId())) {
                cartOrderDetailsDTOFinal.add(cartOrderDetailsDTO);
            }
        }
        Page<CartOrderDetailsDTO> cartOrderDetailsDTOPage = new PageImpl<>(cartOrderDetailsDTOFinal);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            cartOrderDetailsDTOPage
        );
        return ResponseEntity.ok().headers(headers).body(cartOrderDetailsDTOPage.getContent());
    }

    /**
     * {@code GET  /cart-order-details/:id} : get the "id" cartOrderDetails.
     *
     * @param id the id of the cartOrderDetailsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartOrderDetailsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cart-order-details/{id}")
    public ResponseEntity<CartOrderDetailsDTO> getCartOrderDetails(@PathVariable Long id) {
        log.debug("REST request to get CartOrderDetails : {}", id);
        Optional<CartOrderDetailsDTO> cartOrderDetailsDTO = cartOrderDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cartOrderDetailsDTO);
    }

    /**
     * {@code DELETE  /cart-order-details/:id} : delete the "id" cartOrderDetails.
     *
     * @param id the id of the cartOrderDetailsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cart-order-details/{id}")
    public ResponseEntity<Void> deleteCartOrderDetails(@PathVariable Long id) {
        log.debug("REST request to delete CartOrderDetails : {}", id);

        List<CartItems> cartItemsList = cartItemsRepository.findProductsByIdOrder(id).get();
        for (CartItems cartItems : cartItemsList) {
            cartItemsRepository.delete(cartItems);
        }
        if (cartOrderDetailsRepository.findById(id).isPresent()) {
            cartOrderDetailsService.delete(id);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
