package com.localgrower.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartOrderDetailsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartOrderDetailsDTO.class);
        CartOrderDetailsDTO cartOrderDetailsDTO1 = new CartOrderDetailsDTO();
        cartOrderDetailsDTO1.setIdCartOrderDetails(1L);
        CartOrderDetailsDTO cartOrderDetailsDTO2 = new CartOrderDetailsDTO();
        assertThat(cartOrderDetailsDTO1).isNotEqualTo(cartOrderDetailsDTO2);
        cartOrderDetailsDTO2.setIdCartOrderDetails(cartOrderDetailsDTO1.getIdCartOrderDetails());
        assertThat(cartOrderDetailsDTO1).isEqualTo(cartOrderDetailsDTO2);
        cartOrderDetailsDTO2.setIdCartOrderDetails(2L);
        assertThat(cartOrderDetailsDTO1).isNotEqualTo(cartOrderDetailsDTO2);
        cartOrderDetailsDTO1.setIdCartOrderDetails(null);
        assertThat(cartOrderDetailsDTO1).isNotEqualTo(cartOrderDetailsDTO2);
    }
}
