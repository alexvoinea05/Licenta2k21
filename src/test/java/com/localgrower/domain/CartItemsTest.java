package com.localgrower.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartItemsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItems.class);
        CartItems cartItems1 = new CartItems();
        cartItems1.setIdCartItems(1L);
        CartItems cartItems2 = new CartItems();
        cartItems2.setIdCartItems(cartItems1.getIdCartItems());
        assertThat(cartItems1).isEqualTo(cartItems2);
        cartItems2.setIdCartItems(2L);
        assertThat(cartItems1).isNotEqualTo(cartItems2);
        cartItems1.setIdCartItems(null);
        assertThat(cartItems1).isNotEqualTo(cartItems2);
    }
}
