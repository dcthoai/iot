package vn.ptit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "esp32")
public class Esp32Config extends AbstractModel {

    @Column(name = "led_status")
    private Integer ledStatus;

    @Column(name = "lcd_status")
    private Integer lcdStatus;

    @Column(name = "time_refresh")
    private Long timeRefresh;

    @Column(name = "time_analyze")
    private Long timeAnalyze;

    public Esp32Config() {
        super();
    }

    public Integer getLedStatus() {
        return ledStatus;
    }

    public void setLedStatus(Integer ledStatus) {
        this.ledStatus = ledStatus;
    }

    public Integer getLcdStatus() {
        return lcdStatus;
    }

    public void setLcdStatus(Integer lcdStatus) {
        this.lcdStatus = lcdStatus;
    }

    public Long getTimeRefresh() {
        return timeRefresh;
    }

    public void setTimeRefresh(Long timeRefresh) {
        this.timeRefresh = timeRefresh;
    }

    public Long getTimeAnalyze() {
        return timeAnalyze;
    }

    public void setTimeAnalyze(Long timeAnalyze) {
        this.timeAnalyze = timeAnalyze;
    }
}
