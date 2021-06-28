package com.localgrower.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = new Product();
        product1.setIdProduct(1L);
        Product product2 = new Product();
        product2.setIdProduct(product1.getIdProduct());
        assertThat(product1).isEqualTo(product2);
        product2.setIdProduct(2L);
        assertThat(product1).isNotEqualTo(product2);
        product1.setIdProduct(null);
        assertThat(product1).isNotEqualTo(product2);
    }
}
