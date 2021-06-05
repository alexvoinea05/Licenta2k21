package com.localgrower.service.dto.custom;

import com.localgrower.config.Constants;
import com.localgrower.domain.Authority;
import com.localgrower.domain.User;
import com.localgrower.web.rest.vm.ManagedUserVM;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.*;

/**
 * A DTO representing a user, with his authorities.
 */
public class RegisterDTO extends ManagedUserVM {

    public static final int ADRESS_MIN_LENGTH = 5;

    public static final int ADRESS_MAX_LENGTH = 100;

    @Size(min = ADRESS_MIN_LENGTH, max = ADRESS_MAX_LENGTH)
    private String adress;

    public RegisterDTO() {
        // Empty constructor needed for Jackson.
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    @Override
    public String toString() {
        return "RegisterDTO{" + "adress='" + adress + '\'' + '}';
    }
}
