package vn.ptit.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import vn.ptit.common.Constants;
import vn.ptit.config.WebSocketConfig;
import vn.ptit.dto.response.Esp32ConfigDTO;
import vn.ptit.dto.response.ResponseJSON;
import vn.ptit.model.Esp32Config;
import vn.ptit.service.impl.DataAnalysisTask;
import vn.ptit.service.impl.Esp32ConfigService;

import java.util.Objects;

@Controller
@RequestMapping("/api/esp32")
public class WebSocketController {

    @Autowired
    private WebSocketConfig webSocketConfig;

    @Autowired
    private Esp32ConfigService esp32ConfigService;

    @Autowired
    private DataAnalysisTask dataAnalysisTask;

    @PostMapping("/led/on")
    public ResponseEntity<?> turnOnLedESP32() {
        String responseEsp32 = ResponseJSON.socketOk("Turn on LED", Constants.LED_ON);

        try {
            webSocketConfig.sendToESP32(responseEsp32);
            Esp32Config esp32Config = esp32ConfigService.getEsp32Config();
            esp32Config.setLedStatus(1);
            esp32ConfigService.updateEsp32Config(esp32Config);

            return ResponseJSON.ok("Turn on LED success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/led/off")
    public ResponseEntity<?> turnOffLedESP32() {
        String responseEsp32 = ResponseJSON.socketOk("Turn off LED", Constants.LED_OFF);

        try {
            webSocketConfig.sendToESP32(responseEsp32);
            Esp32Config esp32Config = esp32ConfigService.getEsp32Config();
            esp32Config.setLedStatus(0);
            esp32ConfigService.updateEsp32Config(esp32Config);

            return ResponseJSON.ok("Turn off LED success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/lcd/on")
    public ResponseEntity<?> turnOnEsp32LCD() {
        String responseEsp32 = ResponseJSON.socketOk("Turn on LCD", Constants.LCD_ON);

        try {
            webSocketConfig.sendToESP32(responseEsp32);
            Esp32Config esp32Config = esp32ConfigService.getEsp32Config();
            esp32Config.setLcdStatus(1);
            esp32ConfigService.updateEsp32Config(esp32Config);

            return ResponseJSON.ok("Turn on LCD success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/lcd/off")
    public ResponseEntity<?> turnOffEsp32LCD() {
        String responseEsp32 = ResponseJSON.socketOk("Turn off LCD", Constants.LCD_OFF);

        try {
            webSocketConfig.sendToESP32(responseEsp32);
            Esp32Config esp32Config = esp32ConfigService.getEsp32Config();
            esp32Config.setLcdStatus(0);
            esp32ConfigService.updateEsp32Config(esp32Config);

            return ResponseJSON.ok("Turn off LCD success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/lcd/notify")
    public ResponseEntity<?> notifyEsp32LCD(@RequestParam String message) {
        String responseEsp32 = ResponseJSON.socketOk(message, Constants.NOTIFY);

        try {
            webSocketConfig.sendToESP32(responseEsp32);
            return ResponseJSON.ok("Notify to LCD success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/change-refresh-data-time")
    public ResponseEntity<?> changeRefreshDataTime(@RequestParam Long millisecond) {
        try {
            if (Objects.nonNull(millisecond) && millisecond > 0) {
                String responseEsp32 = ResponseJSON.socketOk(String.valueOf(millisecond), Constants.CHANGE_REFRESH_TIME);
                webSocketConfig.sendToESP32(responseEsp32);

                Esp32Config esp32Config = esp32ConfigService.getEsp32Config();
                esp32Config.setTimeRefresh(millisecond);
                esp32ConfigService.updateEsp32Config(esp32Config);

                return ResponseJSON.ok("Change refresh data time success");
            }

            return ResponseJSON.badRequest("Missing millisecond from request");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/change-time-analyze")
    public ResponseEntity<?> changeTimeAnalyze(@RequestParam Long seconds) {
        try {
            Esp32Config esp32Config = esp32ConfigService.getEsp32Config();
            esp32Config.setTimeAnalyze(seconds);
            esp32ConfigService.updateEsp32Config(esp32Config);

            dataAnalysisTask.restartAnalyzeTask();
            return ResponseJSON.ok("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseJSON.serverError("Failed to change. ");
        }
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

            return ResponseJSON.ok("Success", new Gson().toJson(esp32ConfigDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseJSON.badRequest("Failed to get esp32 config");
        }
    }
}
