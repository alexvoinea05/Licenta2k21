package com.localgrower.service.custom;

import java.math.BigDecimal;

public class GasPriceHandler {

    WebScrapper webScrapper;

    public GasPriceHandler(String url) {
        webScrapper = new WebScrapper(url);
    }

    public String getGasPrice() {
        return webScrapper.getData("\"box_pret\">", "ron");
    }
}
