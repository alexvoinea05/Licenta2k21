package com.localgrower.service.impl;

import com.localgrower.domain.CartOrderDetails;
import com.localgrower.repository.CartOrderDetailsRepository;
import com.localgrower.service.CartOrderDetailsService;
import com.localgrower.service.dto.CartOrderDetailsDTO;
import com.localgrower.service.mapper.CartOrderDetailsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CartOrderDetails}.
 */
@Service
@Transactional
public class CartOrderDetailsServiceImpl implements CartOrderDetailsService {

    private final Logger log = LoggerFactory.getLogger(CartOrderDetailsServiceImpl.class);

    private final CartOrderDetailsRepository cartOrderDetailsRepository;

    private final CartOrderDetailsMapper cartOrderDetailsMapper;

    public CartOrderDetailsServiceImpl(
        CartOrderDetailsRepository cartOrderDetailsRepository,
        CartOrderDetailsMapper cartOrderDetailsMapper
    ) {
        this.cartOrderDetailsRepository = cartOrderDetailsRepository;
        this.cartOrderDetailsMapper = cartOrderDetailsMapper;
    }

    @Override
    public CartOrderDetailsDTO save(CartOrderDetailsDTO cartOrderDetailsDTO) {
        log.debug("Request to save CartOrderDetails : {}", cartOrderDetailsDTO);
        CartOrderDetails cartOrderDetails = cartOrderDetailsMapper.toEntity(cartOrderDetailsDTO);
        cartOrderDetails = cartOrderDetailsRepository.save(cartOrderDetails);
        return cartOrderDetailsMapper.toDto(cartOrderDetails);
    }

    @Override
    public Optional<CartOrderDetailsDTO> partialUpdate(CartOrderDetailsDTO cartOrderDetailsDTO) {
        log.debug("Request to partially update CartOrderDetails : {}", cartOrderDetailsDTO);

        return cartOrderDetailsRepository
            .findById(cartOrderDetailsDTO.getIdCartOrderDetails())
            .map(
                existingCartOrderDetails -> {
                    cartOrderDetailsMapper.partialUpdate(existingCartOrderDetails, cartOrderDetailsDTO);
                    return existingCartOrderDetails;
                }
            )
            .map(cartOrderDetailsRepository::save)
            .map(cartOrderDetailsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartOrderDetailsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CartOrderDetails");
        return cartOrderDetailsRepository.findAll(pageable).map(cartOrderDetailsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CartOrderDetailsDTO> findOne(Long id) {
        log.debug("Request to get CartOrderDetails : {}", id);
        return cartOrderDetailsRepository.findById(id).map(cartOrderDetailsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CartOrderDetails : {}", id);
        cartOrderDetailsRepository.deleteById(id);
    }
}
