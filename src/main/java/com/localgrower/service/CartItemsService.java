package com.localgrower.service;

import com.localgrower.service.dto.CartItemsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.localgrower.domain.CartItems}.
 */
public interface CartItemsService {
    /**
     * Save a cartItems.
     *
     * @param cartItemsDTO the entity to save.
     * @return the persisted entity.
     */
    CartItemsDTO save(CartItemsDTO cartItemsDTO);

    /**
     * Partially updates a cartItems.
     *
     * @param cartItemsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CartItemsDTO> partialUpdate(CartItemsDTO cartItemsDTO);

    /**
     * Get all the cartItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CartItemsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" cartItems.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CartItemsDTO> findOne(Long id);

    /**
     * Delete the "id" cartItems.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
