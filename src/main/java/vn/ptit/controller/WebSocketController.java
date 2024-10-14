package vn.ptit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import vn.ptit.common.Constants;
import vn.ptit.config.WebSocketConfig;
import vn.ptit.dto.response.ResponseJSON;
import vn.ptit.model.Esp32Config;
import vn.ptit.service.impl.Esp32ConfigService;

import java.util.Objects;

@Controller
@RequestMapping("/api/esp32")
public class WebSocketController {

    @Autowired
    private WebSocketConfig webSocketConfig;

    @Autowired
    private Esp32ConfigService esp32ConfigService;

    @PostMapping("/led/on")
    public ResponseEntity<?> turnOnLedESP32() {
        String responseEsp32 = ResponseJSON.socketOk("Turn on LED", Constants.LED_ON);

        try {
            System.out.println("\nUpdate ESP32 config: LED_ON");
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
            System.out.println("\nUpdate ESP32 config: LED_OFF");
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
            System.out.println("\nUpdate ESP32 config: LCD_ON");
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
            System.out.println("\nUpdate ESP32 config: LCD_OFF");
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
            System.out.println("\nSend notify to ESP32: " + message);
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
                String responseEsp32 = ResponseJSON.socketOk(millisecond.toString(), Constants.CHANGE_REFRESH_TIME);

                System.out.println("\nUpdate ESP32 config: refresh time of DTH11 = " + millisecond + "ms");
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
}
