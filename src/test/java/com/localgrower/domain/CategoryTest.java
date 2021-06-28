package com.localgrower.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = new Category();
        category1.setIdCategory(1L);
        Category category2 = new Category();
        category2.setIdCategory(category1.getIdCategory());
        assertThat(category1).isEqualTo(category2);
        category2.setIdCategory(2L);
        assertThat(category1).isNotEqualTo(category2);
        category1.setIdCategory(null);
        assertThat(category1).isNotEqualTo(category2);
    }
}
