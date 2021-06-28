package com.localgrower.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoryDTO.class);
        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setIdCategory(1L);
        CategoryDTO categoryDTO2 = new CategoryDTO();
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
        categoryDTO2.setIdCategory(categoryDTO1.getIdCategory());
        assertThat(categoryDTO1).isEqualTo(categoryDTO2);
        categoryDTO2.setIdCategory(2L);
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
        categoryDTO1.setIdCategory(null);
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
    }
}
