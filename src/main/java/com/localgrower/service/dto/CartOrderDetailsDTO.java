package com.localgrower.service.dto;

import com.localgrower.domain.CartItems;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.localgrower.domain.CartOrderDetails} entity.
 */
public class CartOrderDetailsDTO implements Serializable {

    private Long idCartOrderDetails;

    @DecimalMin(value = "0.1")
    private BigDecimal totalPrice;

    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;

    private String statusCommand;

    private AppUserDTO idAppUser;

    private Set<CartItems> cartItems = new HashSet<>();

    public Long getIdCartOrderDetails() {
        return idCartOrderDetails;
    }

    public void setIdCartOrderDetails(Long idCartOrderDetails) {
        this.idCartOrderDetails = idCartOrderDetails;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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

    public String getStatusCommand() {
        return statusCommand;
    }

    public void setStatusCommand(String statusCommand) {
        this.statusCommand = statusCommand;
    }

    public AppUserDTO getIdAppUser() {
        return idAppUser;
    }

    public void setIdAppUser(AppUserDTO idAppUser) {
        this.idAppUser = idAppUser;
    }

    public Set<CartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Set<CartItems> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartOrderDetailsDTO)) {
            return false;
        }

        CartOrderDetailsDTO cartOrderDetailsDTO = (CartOrderDetailsDTO) o;
        if (this.idCartOrderDetails == null) {
            return false;
        }
        return Objects.equals(this.idCartOrderDetails, cartOrderDetailsDTO.idCartOrderDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idCartOrderDetails);
    }

    @Override
    public String toString() {
        return (
            "CartOrderDetailsDTO{" +
            "idCartOrderDetails=" +
            idCartOrderDetails +
            ", totalPrice=" +
            totalPrice +
            ", createdAt=" +
            createdAt +
            ", modifiedAt=" +
            modifiedAt +
            ", statusCommand='" +
            statusCommand +
            '\'' +
            ", idAppUser=" +
            idAppUser +
            ", cartItems=" +
            cartItems +
            '}'
        );
    }
}
