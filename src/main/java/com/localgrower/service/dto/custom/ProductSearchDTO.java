package com.localgrower.service.dto.custom;

import java.math.BigDecimal;

public class ProductSearchDTO {

    private String numeProdus;

    private BigDecimal quantity;

    public String getNumeProdus() {
        return numeProdus;
    }

    public void setNumeProdus(String numeProdus) {
        this.numeProdus = numeProdus;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductSearchDTO{" + "numeProdus='" + numeProdus + '\'' + ", quantity=" + quantity + '}';
    }

    public ProductSearchDTO(String numeProdus, BigDecimal quantity) {
        this.numeProdus = numeProdus;
        this.quantity = quantity;
    }

    public ProductSearchDTO() {}
}
