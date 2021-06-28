package com.localgrower.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartItemsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItemsDTO.class);
        CartItemsDTO cartItemsDTO1 = new CartItemsDTO();
        cartItemsDTO1.setIdCartItems(1L);
        CartItemsDTO cartItemsDTO2 = new CartItemsDTO();
        assertThat(cartItemsDTO1).isNotEqualTo(cartItemsDTO2);
        cartItemsDTO2.setIdCartItems(cartItemsDTO1.getIdCartItems());
        assertThat(cartItemsDTO1).isEqualTo(cartItemsDTO2);
        cartItemsDTO2.setIdCartItems(2L);
        assertThat(cartItemsDTO1).isNotEqualTo(cartItemsDTO2);
        cartItemsDTO1.setIdCartItems(null);
        assertThat(cartItemsDTO1).isNotEqualTo(cartItemsDTO2);
    }
}
