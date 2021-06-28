package com.localgrower.service;

import com.localgrower.service.dto.CartOrderDetailsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.localgrower.domain.CartOrderDetails}.
 */
public interface CartOrderDetailsService {
    /**
     * Save a cartOrderDetails.
     *
     * @param cartOrderDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    CartOrderDetailsDTO save(CartOrderDetailsDTO cartOrderDetailsDTO);

    /**
     * Partially updates a cartOrderDetails.
     *
     * @param cartOrderDetailsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CartOrderDetailsDTO> partialUpdate(CartOrderDetailsDTO cartOrderDetailsDTO);

    /**
     * Get all the cartOrderDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CartOrderDetailsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" cartOrderDetails.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CartOrderDetailsDTO> findOne(Long id);

    /**
     * Delete the "id" cartOrderDetails.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
