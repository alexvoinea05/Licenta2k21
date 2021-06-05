package com.localgrower.service.dto;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.localgrower.domain.CartItems} entity.
 */
@ApiModel(description = "not an ignored comment")
public class CartItemsDTO implements Serializable {

    private Long idCartItems;

    @NotNull
    @DecimalMin(value = "0.1")
    private BigDecimal quantity;

    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;

    private ProductDTO idProduct;

    private CartOrderDetailsDTO idOrderDetails;

    public Long getIdCartItems() {
        return idCartItems;
    }

    public void setIdCartItems(Long idCartItems) {
        this.idCartItems = idCartItems;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
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

    public ProductDTO getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(ProductDTO idProduct) {
        this.idProduct = idProduct;
    }

    public CartOrderDetailsDTO getIdOrderDetails() {
        return idOrderDetails;
    }

    public void setIdOrderDetails(CartOrderDetailsDTO idOrderDetails) {
        this.idOrderDetails = idOrderDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartItemsDTO)) {
            return false;
        }

        CartItemsDTO cartItemsDTO = (CartItemsDTO) o;
        if (this.idCartItems == null) {
            return false;
        }
        return Objects.equals(this.idCartItems, cartItemsDTO.idCartItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idCartItems);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartItemsDTO{" +
            "idCartItems=" + getIdCartItems() +
            ", quantity=" + getQuantity() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            ", idProduct=" + getIdProduct() +
            ", idOrderDetails=" + getIdOrderDetails() +
            "}";
    }
}
