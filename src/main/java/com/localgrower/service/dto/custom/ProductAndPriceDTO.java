package com.localgrower.service.dto.custom;

import com.localgrower.service.dto.ProductDTO;
import java.math.BigDecimal;

public class ProductAndPriceDTO extends ProductDTO {

    private BigDecimal pretTotal;

    public BigDecimal getPretTotal() {
        return pretTotal;
    }

    public void setPretTotal(BigDecimal pretTotal) {
        this.pretTotal = pretTotal;
    }

    public ProductAndPriceDTO() {}

    @Override
    public String toString() {
        return "ProductAndPriceDTO{" + "pretTotal=" + pretTotal + '}';
    }

    public ProductAndPriceDTO(BigDecimal pretTotal) {
        this.pretTotal = pretTotal;
    }
}
