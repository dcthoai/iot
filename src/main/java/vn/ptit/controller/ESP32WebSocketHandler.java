package vn.ptit.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import vn.ptit.dto.request.SensorDataRequest;
import vn.ptit.dto.response.ResponseJSON;
import vn.ptit.service.impl.DataAnalysisTask;
import vn.ptit.service.impl.SensorService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@SuppressWarnings("unused")
public class ESP32WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> esp32Sessions = new HashMap<>();

    @Autowired
    private SensorService sensorService;

    @Autowired
    private DataAnalysisTask dataAnalysisTask;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        esp32Sessions.put(session.getId(), session);
        dataAnalysisTask.startAnalysisTask(esp32Sessions); // Start analyze temperature and humidity data
        System.out.println("New ESP32 device connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        esp32Sessions.remove(session.getId());
        dataAnalysisTask.stopAnalysisTask(); // Cancel analyze service
        System.out.println("ESP32 disconnected: " + session.getId());
    }

    // Method to send a message to all connected ESP32 devices
    public void sendMessageToESP32(String message) throws Exception {
        for (WebSocketSession session : esp32Sessions.values()) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
                System.out.println("Send to ESP32: " + session.getId());
            }
        }
    }

    @Override
    @SuppressWarnings("unused")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received from ESP32: " + message.getPayload());

        try {
            Gson gson = new Gson();
            SensorDataRequest sensorData = gson.fromJson(message.getPayload(), SensorDataRequest.class);

            if (Objects.isNull(sensorData)) {
                System.out.println("Missing sensor data from client request!");
                session.sendMessage(new TextMessage(ResponseJSON.socketError("Missing sensor data from client request!")));
            }

            Integer recordId = sensorService.saveDataStatistic(sensorData.getTemperature(), sensorData.getHumidity());

            if (Objects.nonNull(recordId) && recordId > 0) {
                System.out.println("Save sensor data successfully!");
                session.sendMessage(new TextMessage(ResponseJSON.socketOk("Save data successfully!", -1)));
            } else {
                System.out.println("Internal server error. Failed to save data!");
                session.sendMessage(new TextMessage(ResponseJSON.socketError("Internal server error. Failed to save data!")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.sendMessage(new TextMessage(ResponseJSON.socketError("Server exception. Failed to save data!")));
        }
    }
}
