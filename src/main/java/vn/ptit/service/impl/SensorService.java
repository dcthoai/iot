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
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
public class SensorService implements ISensorService {

    private final SensorRepository sensorRepository;
    private Integer ledLevelAction = 1;

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

    public List<SensorDataDTO> getAveragedData(Timestamp fromDate, Timestamp toDate, Integer durationTime, Integer type) {
        String fromDateStr = Common.convertTimestampToString(fromDate);
        String toDateStr = Common.convertTimestampToString(toDate);
        List<Sensor> sensors = sensorRepository.getSensorDataByDate(fromDateStr, toDateStr);
        List<SensorDataDTO> averagedDataList = new ArrayList<>();

        // Convert Timestamp to Instant for processing
        Instant currentTime = fromDate.toInstant();
        Instant toDateInstant = toDate.toInstant();

        while (currentTime.isBefore(toDateInstant) || !currentTime.isAfter(toDateInstant)) {
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
            if (!dataForDuration.isEmpty()) {
                SensorDataDTO averagedData = new SensorDataDTO();

                averagedData.setTemperature((float) avgTemperature);
                averagedData.setHumidity((float) avgHumidity);
                averagedData.setCreatedDate(dataForDuration.get(0).getCreatedDate());

                averagedDataList.add(averagedData);
            }

            // Continue with next time
            currentTime = nextTime;
        }

        return averagedDataList;
    }

    @Override
    public List<SensorDataDTO> getDataStatistic(Integer type) {
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

        return getAveragedData(fromDate, toDate, durationTime, type);
    }

    @Override
    public String analyzeData(String fromDate, String toDate) {
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
        Timestamp firstTimeAnalysis = sensorsDataList.get(0).getCreatedDate();
        Timestamp lastTimeAnalysis = sensorsDataList.get(sensorsDataList.size() - 1).getCreatedDate();

        JSONObject analysisResult = new JSONObject();
        analysisResult.put("avgTemperature", avgTemperature.orElse(0.0));
        analysisResult.put("avgHumidity", avgHumidity.orElse(0.0));
        analysisResult.put("maxTemperature", maxTemperature);
        analysisResult.put("minTemperature", minTemperature);
        analysisResult.put("maxHumidity", maxHumidity);
        analysisResult.put("minHumidity", minHumidity);
        analysisResult.put("firstTimeAnalysis", firstTimeAnalysis.toString());
        analysisResult.put("lastTimeAnalysis", lastTimeAnalysis.toString());

        return analysisResult.toString();
    }

    @Override
    public String getWeatherForecast() {
        // Analysis data
        Timestamp toDate = Timestamp.from(Instant.now());
        Timestamp fromDate = Timestamp.from(ZonedDateTime.now().minusMinutes(Constants.ANALYZE_REFRESH_DATA_TIME).toInstant());
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
                condition.append("It's very hot. ");
            } else if (avgTemp > 25) {
                condition.append("The weather is warm. ");
            } else if (avgTemp < 15) {
                condition.append("It's quite cold. ");
            }

            // Analyze humidity
            if (avgHum > 80) {
                condition.append("High chance of rain. ");
            } else if (avgHum < 30) {
                condition.append("The air is very dry. ");
            } else if (avgHum < 50) {
                condition.append("The air is a bit dry. ");
            }

            if (avgTemp > 35 || avgTemp < 15 || avgHum < 50 || avgHum > 80) {
                this.ledLevelAction = Constants.LED_RED_COLOR;  // Bad weather
            } else if (avgTemp < 28 && avgTemp > 24 && avgHum > 60 && avgHum < 70) {
                this.ledLevelAction = Constants.LED_GREEN_COLOR; // Good weather
            } else {
                this.ledLevelAction = Constants.LED_GOLD_COLOR; // Normal weather
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
}
