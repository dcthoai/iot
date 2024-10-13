package vn.ptit.service;

import vn.ptit.dto.response.SensorDataDTO;

import java.util.List;

public interface ISensorService {

    Integer saveDataStatistic(Float temperature, Float humidity);

    List<SensorDataDTO> getDataStatistic(Integer type);

    String analyzeData(String fromDate, String toDate);

    String getWeatherForecast();
}
