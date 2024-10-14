package vn.ptit.controller;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ptit.dto.response.Esp32ConfigDTO;
import vn.ptit.dto.response.SensorDataDTO;
import vn.ptit.dto.response.ResponseJSON;
import vn.ptit.model.Esp32Config;
import vn.ptit.service.impl.DataAnalysisTask;
import vn.ptit.service.impl.Esp32ConfigService;
import vn.ptit.service.impl.SensorService;

import java.util.List;
import java.util.Objects;

@RestController
@SuppressWarnings("unused")
@RequestMapping("/api/sensor")
public class SensorDataController {

    private final SensorService sensorService;
    private final Esp32ConfigService esp32ConfigService;
    private final DataAnalysisTask dataAnalysisTask;

    public SensorDataController(SensorService sensorService,
                                Esp32ConfigService esp32ConfigService,
                                DataAnalysisTask dataAnalysisTask) {
        this.sensorService = sensorService;
        this.esp32ConfigService = esp32ConfigService;
        this.dataAnalysisTask = dataAnalysisTask;
    }

    @GetMapping("/statistic-data")
    public ResponseEntity<?> getDataStatistic(@RequestParam Integer type) {
        if (Objects.isNull(type) || type < 1 || type > 4)
            return ResponseJSON.badRequest("Missing type data analyze.");

        List<SensorDataDTO> results = sensorService.getDataStatistic(type);
        return ResponseJSON.ok("Get data successfully.", results);
    }

    @GetMapping("/analyze")
    public String getDataAnalyze(@RequestParam Integer type) {
        return sensorService.getAnalyzeListData(type);
    }

    @GetMapping("/config")
    public ResponseEntity<?> getEsp32Config() {
        try {
            Esp32Config esp32Config = esp32ConfigService.getEsp32Config();
            Esp32ConfigDTO esp32ConfigDTO = new Esp32ConfigDTO();

            esp32ConfigDTO.setLcdStatus(esp32Config.getLcdStatus());
            esp32ConfigDTO.setLedStatus(esp32Config.getLedStatus());
            esp32ConfigDTO.setTimeRefreshData(esp32Config.getTimeRefresh());
            esp32ConfigDTO.setTimeAnalyze(esp32Config.getTimeAnalyze());

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new Gson().toJson(esp32ConfigDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseJSON.badRequest("Failed to get esp32 config");
        }
    }

    @PostMapping("/change-time-analyze")
    public ResponseEntity<?> changeTimeAnalyze(@RequestParam Long seconds) {
        try {
            Esp32Config esp32Config = esp32ConfigService.getEsp32Config();
            esp32Config.setTimeAnalyze(seconds);
            boolean isSuccess = esp32ConfigService.updateEsp32Config(esp32Config);

            if (isSuccess) {
                System.out.println("\nUpdate timeout for data analysis task: " + seconds + "s");
                dataAnalysisTask.restartAnalyzeTask();
            }

            return ResponseJSON.ok("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseJSON.serverError("Failed to change. ");
        }
    }
}
