package vn.ptit.dto.response;

public class Esp32ConfigDTO {

    private Long timeRefreshData, timeAnalyze;
    private Integer ledStatus, lcdStatus;

    public Esp32ConfigDTO() {}

    public Long getTimeRefreshData() {
        return timeRefreshData;
    }

    public void setTimeRefreshData(Long timeRefreshData) {
        this.timeRefreshData = timeRefreshData;
    }

    public Long getTimeAnalyze() {
        return timeAnalyze;
    }

    public void setTimeAnalyze(Long timeAnalyze) {
        this.timeAnalyze = timeAnalyze;
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
}
