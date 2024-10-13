package vn.ptit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ptit.dto.response.SensorDataDTO;
import vn.ptit.dto.response.ResponseJSON;
import vn.ptit.service.impl.SensorService;

import java.util.List;
import java.util.Objects;

@RestController
@SuppressWarnings("unused")
@RequestMapping("/api/statistic")
public class SensorDataController {

    private final SensorService sensorService;

    public SensorDataController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping
    public ResponseEntity<?> getDataStatistic(@RequestParam Integer type) {
        if (Objects.isNull(type) || type < 1 || type > 4)
            return ResponseJSON.badRequest("Missing type data analyze.");

        List<SensorDataDTO> results = sensorService.getDataStatistic(type);
        return ResponseJSON.ok("Get data successfully.", results);
    }

    @GetMapping("/analyze")
    public ResponseEntity<?> getDataAnalyze(@RequestParam String fromDate,
                                            @RequestParam String toDate) {
        String results = sensorService.analyzeData(fromDate, toDate);
        return ResponseJSON.ok("Get data analyze successfully.", results);
    }
}
