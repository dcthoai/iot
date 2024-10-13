package vn.ptit.dto.response;

import vn.ptit.common.Common;

import java.sql.Timestamp;

public class SensorDataDTO {

    private Float temperature, humidity;
    private String createdDate;

    public SensorDataDTO() {}

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = (float) (Math.round(temperature * 100.0) / 100.0);
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = (float) (Math.round(humidity * 100.0) / 100.0);
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = Common.convertTimestampToString(createdDate);
    }
}
