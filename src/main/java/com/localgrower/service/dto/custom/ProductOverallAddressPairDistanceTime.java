package com.localgrower.service.dto.custom;

import com.localgrower.domain.custom.AddressPair;
import java.math.BigDecimal;

public class ProductOverallAddressPairDistanceTime {

    private Long idProdus;

    private String numeProdus;

    private BigDecimal pretTotal;

    private AddressPair addressPair;

    private DistanceTimeDTO distanceTimeDTO;

    public Long getIdProdus() {
        return idProdus;
    }

    public void setIdProdus(Long idProdus) {
        this.idProdus = idProdus;
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

    public AddressPair getAddressPair() {
        return addressPair;
    }

    public void setAddressPair(AddressPair addressPair) {
        this.addressPair = addressPair;
    }

    public DistanceTimeDTO getDistanceTimeDTO() {
        return distanceTimeDTO;
    }

    public void setDistanceTimeDTO(DistanceTimeDTO distanceTimeDTO) {
        this.distanceTimeDTO = distanceTimeDTO;
    }

    public ProductOverallAddressPairDistanceTime() {}

    public ProductOverallAddressPairDistanceTime(
        Long idProdus,
        String numeProdus,
        BigDecimal pretTotal,
        AddressPair addressPair,
        DistanceTimeDTO distanceTimeDTO
    ) {
        this.idProdus = idProdus;
        this.numeProdus = numeProdus;
        this.pretTotal = pretTotal;
        this.addressPair = addressPair;
        this.distanceTimeDTO = distanceTimeDTO;
    }

    @Override
    public String toString() {
        return (
            "ProductOverallAddressPairDistanceTime{" +
            "idProdus=" +
            idProdus +
            ", numeProdus='" +
            numeProdus +
            '\'' +
            ", pretTotal=" +
            pretTotal +
            ", addressPair=" +
            addressPair +
            ", distanceTimeDTO=" +
            distanceTimeDTO +
            '}'
        );
    }
}
