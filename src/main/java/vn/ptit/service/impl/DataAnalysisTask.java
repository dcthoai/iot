package vn.ptit.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import vn.ptit.common.Constants;
import vn.ptit.dto.response.ResponseJSON;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("unused")
public class DataAnalysisTask {

    private final SensorService sensorService;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;
    private Map<String, WebSocketSession> esp32Sessions;

    public DataAnalysisTask(SensorService sensorService) {
        this.sensorService = sensorService;

        // Create thread pool with 1 thread to schedule task
        System.out.println("Creating a new thread to analyze data....");
        scheduler = Executors.newScheduledThreadPool(1);
    }

    // Active schedule task
    public void startAnalysisTask(Map<String, WebSocketSession> esp32Sessions) {
        this.esp32Sessions = esp32Sessions;

        if (scheduledTask == null || scheduledTask.isCancelled()) {
            System.out.println("Starting data analysis task...");

            scheduledTask = scheduler.scheduleAtFixedRate(this::analyzeAndSendData, 0, Constants.ANALYZE_REFRESH_DATA_TIME, TimeUnit.MINUTES);
        }
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
                String ledNotify = ResponseJSON.socketOk(ledAction.toString(), Constants.LED_NOTIFY);

                try {
                    session.sendMessage(new TextMessage(notify));
                    session.sendMessage(new TextMessage(ledNotify));

                    System.out.println("Send notify to ESP32 after analyze data: " + session.getId());
                    System.out.println("Message: " + message);
                    System.out.println("Led notify: " + ledAction);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
