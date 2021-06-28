package com.localgrower.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartOrderDetailsMapperTest {

    private CartOrderDetailsMapper cartOrderDetailsMapper;

    @BeforeEach
    public void setUp() {
        cartOrderDetailsMapper = new CartOrderDetailsMapperImpl();
    }
}
