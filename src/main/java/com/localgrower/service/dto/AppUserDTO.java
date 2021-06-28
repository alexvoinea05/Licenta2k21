package com.localgrower.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.localgrower.domain.AppUser} entity.
 */
public class AppUserDTO implements Serializable {

    private Long idAppUser;

    private String certificateUrl;

    @NotNull
    private String adress;

    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;

    private UserDTO user;

    public Long getIdAppUser() {
        return idAppUser;
    }

    public void setIdAppUser(Long idAppUser) {
        this.idAppUser = idAppUser;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUserDTO)) {
            return false;
        }

        AppUserDTO appUserDTO = (AppUserDTO) o;
        if (this.idAppUser == null) {
            return false;
        }
        return Objects.equals(this.idAppUser, appUserDTO.idAppUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idAppUser);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUserDTO{" +
            "idAppUser=" + getIdAppUser() +
            ", certificateUrl='" + getCertificateUrl() + "'" +
            ", adress='" + getAdress() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
