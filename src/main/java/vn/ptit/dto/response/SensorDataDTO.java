package vn.ptit.dto.response;

import vn.ptit.common.Common;

import java.sql.Timestamp;
import java.util.Objects;

public class SensorDataDTO {

    private Double temperature, humidity;
    private String createdDate;

    public SensorDataDTO() {}

    public SensorDataDTO(Double temperature, Double humidity, String createdDate) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.createdDate = createdDate;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        if (Objects.isNull(temperature))
            this.temperature = null;
        else
            this.temperature = Math.round(temperature * 100.0) / 100.0;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        if (Objects.isNull(humidity))
            this.humidity = null;
        else
            this.humidity = Math.round(humidity * 100.0) / 100.0;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        if (Objects.isNull(createdDate))
            this.createdDate = "";
        else
            this.createdDate = Common.convertTimestampToString(createdDate);
    }
}
