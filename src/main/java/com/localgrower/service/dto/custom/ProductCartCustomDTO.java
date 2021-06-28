package com.localgrower.service.dto.custom;

import java.math.BigDecimal;

public class ProductCartCustomDTO {

    private Long idProduct;

    private BigDecimal quantity;

    public ProductCartCustomDTO() {}

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductCartCustomDTO{" + "idProduct=" + idProduct + ", quantity=" + quantity + '}';
    }
}
