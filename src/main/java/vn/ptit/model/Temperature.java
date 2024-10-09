package vn.ptit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.DecimalFormat;

@Entity
@Table(name = "temperature")
public class Temperature extends AbstractModel {

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "humidity")
    private Float humidity;

    public Temperature() {
        super();
    }

    public Temperature(Float temperature, Float humidity) {
        super();
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public Float getTemperature() {
        return Float.valueOf(new DecimalFormat("#.##").format(temperature));
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return Float.valueOf(new DecimalFormat("#.##").format(humidity));
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }
}
