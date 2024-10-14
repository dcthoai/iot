package vn.ptit.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import vn.ptit.common.Constants;
import vn.ptit.dto.response.ResponseJSON;
import vn.ptit.model.Esp32Config;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("unused")
public class DataAnalysisTask {

    private final SensorService sensorService;
    private final Esp32ConfigService esp32ConfigService;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;
    private Map<String, WebSocketSession> esp32Sessions;
    private Long timeToRefreshDataAnalyze = Constants.ANALYZE_REFRESH_DATA_TIME;

    public DataAnalysisTask(SensorService sensorService, Esp32ConfigService esp32ConfigService) {
        this.sensorService = sensorService;
        this.esp32ConfigService = esp32ConfigService;

        // Create thread pool with 1 thread to schedule task
        System.out.println("Creating a new thread to analyze data....");
        scheduler = Executors.newScheduledThreadPool(1);
    }

    // Active schedule task
    public void startAnalysisTask(Map<String, WebSocketSession> esp32Sessions) {
        this.esp32Sessions = esp32Sessions;

        if (scheduledTask == null || scheduledTask.isCancelled()) {
            System.out.println("Starting data analysis task...");
            setupEsp32Config();

            scheduledTask = scheduler.scheduleAtFixedRate(this::analyzeAndSendData, 0, timeToRefreshDataAnalyze, TimeUnit.SECONDS);
        }
    }

    public void restartAnalyzeTask() {
        System.out.println("Restart data analyze task...");
        setupEsp32Config();
        scheduledTask = scheduler.scheduleAtFixedRate(this::analyzeAndSendData, 0, timeToRefreshDataAnalyze, TimeUnit.SECONDS);
    }

    // Cancel task
    public void stopAnalysisTask() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            System.out.println("Stopping data analysis task...");
            scheduledTask.cancel(true);
        }
    }

    // Run every 10 minus (600000 milliseconds)
    public void analyzeAndSendData() {
        String analysisResult = sensorService.getWeatherForecast();

        try {
            sendNotifyToEsp32(analysisResult);
        } catch (Exception e) {
            System.out.println("Cannot analyze sensor data or send message to ESP32.");
            e.printStackTrace();
        }
    }

    public void sendNotifyToEsp32(String message) {
        for (WebSocketSession session : esp32Sessions.values()) {
            if (session.isOpen()) {
                Integer ledAction = sensorService.getLedLevelAction();
                String notify = ResponseJSON.socketOk(message, Constants.NOTIFY);

                try {
                    session.sendMessage(new TextMessage(notify));

                    if (ledAction.equals(Constants.LED_WARNING)) {
                        // Send request to warning LED when bad weather
                        String ledNotify = ResponseJSON.socketOk("Turn on LED warning", Constants.LED_NOTIFY);
                        session.sendMessage(new TextMessage(ledNotify));
                    }

                    System.out.println("\nSend notify to ESP32 after analyze data: " + message + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void sendConfigToEsp32(String message, Integer action) {
        for (WebSocketSession session : esp32Sessions.values()) {
            if (session.isOpen()) {
                String notify = ResponseJSON.socketOk(message, action);

                try {
                    session.sendMessage(new TextMessage(notify));

                    if (action.equals(Constants.CHANGE_REFRESH_TIME))
                        System.out.println("Send config to ESP32: Set refresh time of DTH11 = " + message + "ms");
                    else
                        System.out.println("Send config to ESP32: " + message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setupEsp32Config() {
        Esp32Config esp32Config = esp32ConfigService.getEsp32Config();

        if (Objects.nonNull(esp32Config)) {
            try {
                System.out.println("Get ESP32 default config from database...\n");

                if (esp32Config.getLcdStatus() == 1) {
                    sendConfigToEsp32("Config LCD status: ON", Constants.LCD_ON);
                } else {
                    sendConfigToEsp32("Config LCD status: OFF", Constants.LCD_OFF);
                }

                if (esp32Config.getLedStatus() == 1) {
                    sendConfigToEsp32("Config LED status: ON", Constants.LED_ON);
                } else {
                    sendConfigToEsp32("Config LED status: OFF", Constants.LED_OFF);
                }

                if (esp32Config.getTimeRefresh() > 0) {
                    sendConfigToEsp32(esp32Config.getTimeRefresh().toString(), Constants.CHANGE_REFRESH_TIME);
                }

                if (timeToRefreshDataAnalyze > 0) {
                    this.timeToRefreshDataAnalyze = esp32Config.getTimeAnalyze();
                    sensorService.setTimeAnalyze(esp32Config.getTimeAnalyze());
                    System.out.println("Set timeout for each data analysis and send notify to esp32: " + timeToRefreshDataAnalyze + "s");
                }
            } catch (Exception e) {
                System.out.println("Failed to get ESP32 config.");
                e.printStackTrace();
            }
        }
    }
}
