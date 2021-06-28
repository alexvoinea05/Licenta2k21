package com.localgrower.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartItemsMapperTest {

    private CartItemsMapper cartItemsMapper;

    @BeforeEach
    public void setUp() {
        cartItemsMapper = new CartItemsMapperImpl();
    }
}
