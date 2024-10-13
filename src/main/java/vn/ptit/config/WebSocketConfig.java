package vn.ptit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import vn.ptit.controller.ESP32WebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ESP32WebSocketHandler esp32WebSocketHandler;

    @Autowired
    public WebSocketConfig(ESP32WebSocketHandler esp32WebSocketHandler) {
        this.esp32WebSocketHandler = esp32WebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(esp32WebSocketHandler, "/esp32").setAllowedOrigins("*");
    }

    public void sendToESP32(String message) throws Exception {
        esp32WebSocketHandler.sendMessageToESP32(message);
    }
}
