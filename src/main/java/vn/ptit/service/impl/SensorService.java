package vn.ptit.service.impl;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import vn.ptit.common.Common;
import vn.ptit.common.Constants;
import vn.ptit.dto.response.SensorDataDTO;
import vn.ptit.model.Sensor;
import vn.ptit.repository.SensorRepository;
import vn.ptit.service.ISensorService;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SensorService implements ISensorService {

    private final SensorRepository sensorRepository;
    private Integer ledLevelAction = Constants.LED_NORMAL;
    private Long timeAnalyze;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public Integer saveDataStatistic(Float temperature, Float humidity) {
        Sensor sensor = new Sensor(temperature, humidity);
        sensor.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        Sensor sensorResult = sensorRepository.save(sensor);

        return sensorResult.getId();
    }

    private Map<String, Object> getTimeByType(Integer type) {
        Map<String, Object> times = new HashMap<>();

        ZonedDateTime zonedToDate = ZonedDateTime.now();
        ZonedDateTime zonedFromDate;
        Timestamp toDate = Timestamp.from(Instant.now());
        Timestamp fromDate = toDate;
        Integer durationTime = 5;

        switch (type) {
            case 1:
                zonedFromDate = zonedToDate.minusHours(1);
                fromDate = Timestamp.from(zonedFromDate.toInstant());
                durationTime = Constants.ANALYZE_LAST_HOUR;
                break;
            case 2:
                zonedFromDate = zonedToDate.minusDays(1);
                fromDate = Timestamp.from(zonedFromDate.toInstant());
                durationTime = Constants.ANALYZE_LAST_DAY;
                break;
            case 3:
                zonedFromDate = zonedToDate.minusWeeks(1);
                fromDate = Timestamp.from(zonedFromDate.toInstant());
                durationTime = Constants.ANALYZE_LAST_WEEK;
                break;
            case 4:
                zonedFromDate = zonedToDate.minusMonths(1);
                fromDate = Timestamp.from(zonedFromDate.toInstant());
                durationTime = Constants.ANALYZE_LAST_MONTH;
                break;
        }

        times.put("fromDate", fromDate);
        times.put("toDate", toDate);
        times.put("durationTime", durationTime);

        return times;
    }

    private List<SensorDataDTO> getAveragedData(Timestamp fromDate, Timestamp toDate, Integer durationTime, Integer type) {
        String fromDateStr = Common.convertTimestampToString(fromDate);
        String toDateStr = Common.convertTimestampToString(toDate);
        List<Sensor> sensors = sensorRepository.getSensorDataByDate(fromDateStr, toDateStr);
        List<SensorDataDTO> averagedDataList = new ArrayList<>();

        // Convert Timestamp to Instant for processing
        Instant currentTime = fromDate.toInstant().plus(durationTime, ChronoUnit.MINUTES);
        Instant toDateInstant = toDate.toInstant();

        while (!currentTime.isAfter(toDateInstant)) {
            Instant finalCurrentTime = currentTime;
            Instant nextTime = currentTime.plus(durationTime, ChronoUnit.MINUTES);

            // Get data in duration
            List<Sensor> dataForDuration;

            if (type == 1 || type == 2) {
                dataForDuration = sensors.stream()
                        .filter(sensor ->
                                sensor.getCreatedDate().toInstant().isAfter(finalCurrentTime)
                                        && sensor.getCreatedDate().toInstant().isBefore(nextTime))
                        .collect(Collectors.toList());
            } else {
                dataForDuration = sensors.stream()
                        .filter(sensor ->
                                sensor.getCreatedDate().toLocalDateTime().toLocalDate().isEqual(finalCurrentTime.atZone(ZoneId.systemDefault()).toLocalDate()))
                        .collect(Collectors.toList());
            }

            // Calculate temperature and humidity value
            double avgTemperature = dataForDuration.stream()
                    .mapToDouble(Sensor::getTemperature)
                    .average()
                    .orElse(Double.NaN);
            double avgHumidity = dataForDuration.stream()
                    .mapToDouble(Sensor::getHumidity)
                    .average()
                    .orElse(Double.NaN);

            // Save average data
            SensorDataDTO averagedData = new SensorDataDTO();

            if (!dataForDuration.isEmpty()) {
                averagedData.setTemperature(Double.isNaN(avgTemperature) ? null : avgTemperature);
                averagedData.setHumidity(Double.isNaN(avgHumidity) ? null : avgHumidity);
                averagedData.setCreatedDate(dataForDuration.get(0).getCreatedDate());

                averagedDataList.add(averagedData);
            } else {
                averagedData.setTemperature(null);
                averagedData.setHumidity(null);
                averagedData.setCreatedDate(null);

                averagedDataList.add(averagedData);
            }

            // Continue with next time
            currentTime = nextTime;
        }

        return averagedDataList;
    }

    @Override
    public List<SensorDataDTO> getDataStatistic(Integer type) {
        Map<String, Object> times = getTimeByType(type);

        Integer durationTime = (Integer) times.get("durationTime");
        Timestamp fromDate = (Timestamp) times.get("fromDate");
        Timestamp toDate = (Timestamp) times.get("toDate");

        return getAveragedData(fromDate, toDate, durationTime, type);
    }

    private String analyzeData(String fromDate, String toDate) {
        List<Sensor> sensorsDataList = sensorRepository.getSensorDataByDate(fromDate, toDate);

        if (sensorsDataList == null || sensorsDataList.isEmpty()) {
            System.out.println("No data available for analysis.");
            return "";
        }

        // Calculate average value of temperature and humidity
        OptionalDouble avgTemperature = sensorsDataList.stream()
                .mapToDouble(Sensor::getTemperature)
                .average();

        OptionalDouble avgHumidity = sensorsDataList.stream()
                .mapToDouble(Sensor::getHumidity)
                .average();

        // Max temperature and humidity value
        float maxTemperature = sensorsDataList.stream()
                .map(Sensor::getTemperature)
                .max(Float::compare)
                .orElseThrow(() -> new RuntimeException("No temperature data"));

        float maxHumidity = sensorsDataList.stream()
                .map(Sensor::getHumidity)
                .max(Float::compare)
                .orElseThrow(() -> new RuntimeException("No humidity data"));

        // Min temperature and humidity value
        float minTemperature = sensorsDataList.stream()
                .map(Sensor::getTemperature)
                .min(Float::compare)
                .orElseThrow(() -> new RuntimeException("No temperature data"));

        float minHumidity = sensorsDataList.stream()
                .map(Sensor::getHumidity)
                .min(Float::compare)
                .orElseThrow(() -> new RuntimeException("No humidity data"));

        // First time and last time of data
        Timestamp lastTimeAnalysis = sensorsDataList.get(0).getCreatedDate();
        Timestamp firstTimeAnalysis = sensorsDataList.get(sensorsDataList.size() - 1).getCreatedDate();

        JSONObject analysisResult = new JSONObject();

        float avgTemperatureFormat = (float) avgTemperature.orElse(0.0);
        float avgHumidityFormat = (float) avgHumidity.orElse(0.0);

        analysisResult.put("avgTemperature",  (float) (Math.round(avgTemperatureFormat * 100.0) / 100.0));
        analysisResult.put("avgHumidity", (float) (Math.round(avgHumidityFormat * 100.0) / 100.0));
        analysisResult.put("maxTemperature", maxTemperature);
        analysisResult.put("minTemperature", minTemperature);
        analysisResult.put("maxHumidity", maxHumidity);
        analysisResult.put("minHumidity", minHumidity);
        analysisResult.put("firstTimeAnalysis", Common.convertTimestampToString(firstTimeAnalysis, "HH:mm:ss dd/MM/yyyy"));
        analysisResult.put("lastTimeAnalysis", Common.convertTimestampToString(lastTimeAnalysis, "HH:mm:ss dd/MM/yyyy"));

        return analysisResult.toString();
    }

    @Override
    public String getAnalyzeListData(Integer type) {
        Map<String, Object> times = getTimeByType(type);

        Timestamp fromDate = (Timestamp) times.get("fromDate");
        Timestamp toDate = (Timestamp) times.get("toDate");

        return analyzeData(fromDate.toString(), toDate.toString());
    }

    @Override
    public String getWeatherForecast() {
        // Analysis data
        Timestamp toDate = Timestamp.from(Instant.now());
        Timestamp fromDate = Timestamp.from(ZonedDateTime.now().minusMinutes(timeAnalyze).toInstant());
        String fromDateStr = Common.convertTimestampToString(fromDate);
        String toDateStr = Common.convertTimestampToString(toDate);

        StringBuilder condition = new StringBuilder();
        List<Sensor> sensorsDataList = sensorRepository.getSensorDataByDate(fromDateStr, toDateStr);

        if (sensorsDataList == null || sensorsDataList.isEmpty()) {
            System.out.println("No data available for analysis.");
            return "No notification";
        }

        // Calculate average value of temperature and humidity
        OptionalDouble avgTemperature = sensorsDataList.stream()
                .mapToDouble(Sensor::getTemperature)
                .average();

        OptionalDouble avgHumidity = sensorsDataList.stream()
                .mapToDouble(Sensor::getHumidity)
                .average();

        if (avgTemperature.isPresent() && avgHumidity.isPresent()) {
            double avgTemp = avgTemperature.getAsDouble();
            double avgHum = avgHumidity.getAsDouble();

            // Analyze temperature
            if (avgTemp > 35) {
                condition.append("The temperature is very   hot. ");
            } else if (avgTemp > 25) {
                condition.append("The temperature is normal ");
            } else if (avgTemp >= 15) {
                condition.append("The temperature is so     cool ");
            } else if (avgTemp < 15) {
                condition.append("The temperature is cold.  ");
            }

            // Analyze humidity
            if (avgHum > 80) {
                condition.append("High chance of rain.");
            } else if (avgHum > 50) {
                condition.append("The air is dry. ");
            } else if (avgHum > 30) {
                condition.append("The air is a bit dry.");
            } else if (avgHum <= 30){
                condition.append("The air is a very dry.");
            }

            if (avgTemp > 35 || avgTemp < 15 || avgHum < 30 || avgHum > 80) {
                this.ledLevelAction = Constants.LED_WARNING;  // Bad weather
            } else {
                this.ledLevelAction = Constants.LED_NORMAL; // Normal weather
            }
        }

        if (condition.length() == 0) {
            condition.append("No notification");
        }

        return condition.toString();
    }

    public Integer getLedLevelAction() {
        return ledLevelAction;
    }

    public void setTimeAnalyze(Long timeAnalyze) {
        this.timeAnalyze = timeAnalyze;
    }
}
