package vn.ptit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import vn.ptit.common.Constants;
import vn.ptit.config.WebSocketConfig;
import vn.ptit.dto.response.ResponseJSON;

import java.util.Objects;

@Controller
@RequestMapping("/api/esp32")
public class WebSocketController {

    @Autowired
    private WebSocketConfig webSocketConfig;

    @GetMapping("/led/notify")
    public ResponseEntity<?> notifyESP32Led(@RequestParam Integer ledLevel) {
        try {
            if (Objects.nonNull(ledLevel)) {
                int ledLevelResponse;

                switch (ledLevel) {
                    case 1:
                        ledLevelResponse = Constants.LED_GREEN_COLOR;
                        break;
                    case 2:
                        ledLevelResponse = Constants.LED_GOLD_COLOR;
                        break;
                    case 3:
                        ledLevelResponse = Constants.LED_RED_COLOR;
                        break;
                    default:
                        return ResponseJSON.badRequest("Invalid led level.");
                }

                String responseEsp32 = ResponseJSON.socketOk(String.valueOf(ledLevelResponse), Constants.LED_NOTIFY);
                webSocketConfig.sendToESP32(responseEsp32);
                return ResponseJSON.ok("Change led level successfully!");
            }

            return ResponseJSON.badRequest("Missing led level from request.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/led/on")
    public ResponseEntity<?> turnOnLedESP32() {
        String responseEsp32 = ResponseJSON.socketOk("Turn on LED", Constants.LED_ON);

        try {
            webSocketConfig.sendToESP32(responseEsp32);
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
    public ResponseEntity<?> changeRefreshDataTime(@RequestParam Integer millisecond) {
        try {
            if (Objects.nonNull(millisecond) && millisecond > 0) {
                String responseEsp32 = ResponseJSON.socketOk(String.valueOf(millisecond), Constants.CHANGE_REFRESH_TIME);
                webSocketConfig.sendToESP32(responseEsp32);
                return ResponseJSON.ok("Change refresh data time success");
            }

            return ResponseJSON.badRequest("Missing millisecond from request");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
