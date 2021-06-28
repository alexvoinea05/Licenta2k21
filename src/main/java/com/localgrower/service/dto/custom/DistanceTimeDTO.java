package com.localgrower.service.dto.custom;

import java.math.BigDecimal;
import org.springframework.util.StringUtils;

public class DistanceTimeDTO {

    private String time;

    private String distance;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public DistanceTimeDTO() {}

    @Override
    public String toString() {
        return "DistanceTimeDTO{" + "time='" + time + '\'' + ", distance='" + distance + '\'' + '}';
    }
}
