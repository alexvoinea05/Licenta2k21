package com.localgrower.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.localgrower.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUser.class);
        AppUser appUser1 = new AppUser();
        appUser1.setIdAppUser(1L);
        AppUser appUser2 = new AppUser();
        appUser2.setIdAppUser(appUser1.getIdAppUser());
        assertThat(appUser1).isEqualTo(appUser2);
        appUser2.setIdAppUser(2L);
        assertThat(appUser1).isNotEqualTo(appUser2);
        appUser1.setIdAppUser(null);
        assertThat(appUser1).isNotEqualTo(appUser2);
    }
}
