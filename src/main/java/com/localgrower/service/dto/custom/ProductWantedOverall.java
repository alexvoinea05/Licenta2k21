package com.localgrower.service.dto.custom;

import com.localgrower.domain.custom.AddressPair;
import java.math.BigDecimal;

public class ProductWantedOverall {

    private Long idProdus;

    private String numeProdus;

    private BigDecimal pretTotal;

    private AddressPair addressPair;

    public Long getIdProdus() {
        return idProdus;
    }

    public void setIdProdus(Long idProdus) {
        this.idProdus = idProdus;
    }

    public AddressPair getAddressPair() {
        return addressPair;
    }

    public void setAddressPair(AddressPair addressPair) {
        this.addressPair = addressPair;
    }

    public String getNumeProdus() {
        return numeProdus;
    }

    public void setNumeProdus(String numeProdus) {
        this.numeProdus = numeProdus;
    }

    public BigDecimal getPretTotal() {
        return pretTotal;
    }

    public void setPretTotal(BigDecimal pretTotal) {
        this.pretTotal = pretTotal;
    }

    public ProductWantedOverall(Long idProdus, String numeProdus, BigDecimal pretTotal, AddressPair addressPair) {
        this.idProdus = idProdus;
        this.numeProdus = numeProdus;
        this.pretTotal = pretTotal;
        this.addressPair = addressPair;
    }

    public ProductWantedOverall() {}

    @Override
    public String toString() {
        return (
            "ProductWantedOverall{" +
            "idProdus=" +
            idProdus +
            ", numeProdus='" +
            numeProdus +
            '\'' +
            ", pretTotal=" +
            pretTotal +
            ", addressPair=" +
            addressPair +
            '}'
        );
    }
}
