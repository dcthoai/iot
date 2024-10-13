package vn.ptit.dto.request;

public class SensorDataRequest {

    private Float temperature, humidity;

    public SensorDataRequest() {}

    public SensorDataRequest(Float temperature, Float humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }
}
