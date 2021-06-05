package com.localgrower.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartOrderDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartOrderDetails.class);
        CartOrderDetails cartOrderDetails1 = new CartOrderDetails();
        cartOrderDetails1.setIdCartOrderDetails(1L);
        CartOrderDetails cartOrderDetails2 = new CartOrderDetails();
        cartOrderDetails2.setIdCartOrderDetails(cartOrderDetails1.getIdCartOrderDetails());
        assertThat(cartOrderDetails1).isEqualTo(cartOrderDetails2);
        cartOrderDetails2.setIdCartOrderDetails(2L);
        assertThat(cartOrderDetails1).isNotEqualTo(cartOrderDetails2);
        cartOrderDetails1.setIdCartOrderDetails(null);
        assertThat(cartOrderDetails1).isNotEqualTo(cartOrderDetails2);
    }
}
