package com.localgrower.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductDTO.class);
        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setIdProduct(1L);
        ProductDTO productDTO2 = new ProductDTO();
        assertThat(productDTO1).isNotEqualTo(productDTO2);
        productDTO2.setIdProduct(productDTO1.getIdProduct());
        assertThat(productDTO1).isEqualTo(productDTO2);
        productDTO2.setIdProduct(2L);
        assertThat(productDTO1).isNotEqualTo(productDTO2);
        productDTO1.setIdProduct(null);
        assertThat(productDTO1).isNotEqualTo(productDTO2);
    }
}
