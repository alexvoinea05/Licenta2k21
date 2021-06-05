package com.localgrower.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.localgrower.domain.Product} entity.
 */
public class ProductDTO implements Serializable {

    private Long idProduct;

    @NotNull
    @Size(min = 1)
    private String name;

    @NotNull
    @DecimalMin(value = "0.1")
    private BigDecimal price;

    @NotNull
    @DecimalMin(value = "0.1")
    private BigDecimal stock;

    private String imageUrl;

    private String productUrl;

    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;

    private CategoryDTO idCategory;

    private AppUserDTO idGrower;

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
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

    public CategoryDTO getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(CategoryDTO idCategory) {
        this.idCategory = idCategory;
    }

    public AppUserDTO getIdGrower() {
        return idGrower;
    }

    public void setIdGrower(AppUserDTO idGrower) {
        this.idGrower = idGrower;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.idProduct == null) {
            return false;
        }
        return Objects.equals(this.idProduct, productDTO.idProduct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idProduct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "idProduct=" + getIdProduct() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", stock=" + getStock() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", productUrl='" + getProductUrl() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", modifiedAt='" + getModifiedAt() + "'" +
            ", idCategory=" + getIdCategory() +
            ", idGrower=" + getIdGrower() +
            "}";
    }
}
