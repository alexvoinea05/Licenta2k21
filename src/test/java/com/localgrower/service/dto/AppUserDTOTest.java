package com.localgrower.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUserDTO.class);
        AppUserDTO appUserDTO1 = new AppUserDTO();
        appUserDTO1.setIdAppUser(1L);
        AppUserDTO appUserDTO2 = new AppUserDTO();
        assertThat(appUserDTO1).isNotEqualTo(appUserDTO2);
        appUserDTO2.setIdAppUser(appUserDTO1.getIdAppUser());
        assertThat(appUserDTO1).isEqualTo(appUserDTO2);
        appUserDTO2.setIdAppUser(2L);
        assertThat(appUserDTO1).isNotEqualTo(appUserDTO2);
        appUserDTO1.setIdAppUser(null);
        assertThat(appUserDTO1).isNotEqualTo(appUserDTO2);
    }
}
