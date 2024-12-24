#include <TFT_eSPI.h> // Library for TFT LCD 1.8 inch monitor
#include <SPI.h>  // Library for use SPI protocol
#include <DHT.h>  // Library for temperature sensor DHT11
#include <WiFi.h>
#include <HTTPClient.h>
#include <Arduino.h>
#include <ArduinoJson.h>
#include <ArduinoWebsockets.h>
using namespace websockets;

#define DHTPIN 15  // ESP32 pin to connect to DHT11
#define DHTTYPE DHT11

#define TFT_CS 5  // Chip select control pin
#define TFT_DC 2  // Data Command control pin
#define TFT_RST 4  // Reset pin (could connect to RST pin)

// LED control pin
const int RED_PIN = 19;
const int GREEN_PIN = 16;
const int BLUE_PIN = 21;

// Action execute when have request from Socket server
const int LED_ON = 0;
const int LED_OFF = 1;
const int LED_NOTIFY = 2;
const int LCD_ON = 3;
const int LCD_OFF = 4;
const int NOTIFY = 5;
const int CHANGE_REFRESH_TIME = 6;

bool lcdStatus = true;
bool ledStatus = true;
bool ledWarningStatus = false;
unsigned long timeRefreshData = 5000;
unsigned long startTime;

// Socket connection status
bool connectSocket = false;

// SSID and password of WIFI to connect
#define WIFI_NAME "WIFI_NAME"
#define WIFI_PASSWORD "WIFI_PASSWORD"

// Socket server and ThinkSpeak cloud url
// String THINGSPEAK_SERVER_URL = "https://api.thingspeak.com/update?api_key=your_api_key";
const char* WEBSOCKET_SERVER_URL = "ws://0.tcp.ap.ngrok.io:14775/esp32";

WebsocketsClient client; // Start socket client
TFT_eSPI tft = TFT_eSPI(); // Start LCD monitor
DHT dht(DHTPIN, DHTTYPE); // Start DHT11 sensor

void lcdDisplay(String message, int x, int y, bool isClear) {
  if (isClear) {
    tft.fillRect(0, 0, 160, 60, TFT_BLACK); // Clear screen by filling it with black
  }

  tft.setCursor(x, y);  // Set position to print
  tft.println(message);
}

void lcdNotify(String message) {
  tft.fillRect(0, 60, 160, 128, TFT_BLACK); // Clear area to write message
  tft.setCursor(5, 60);
  tft.println(message);
}

void getTemperatureAndHumidity(float &temperature, float &humidity) {
  humidity = dht.readHumidity();
  temperature = dht.readTemperature();  // Read temperature by Celsius

  if (isnan(humidity) || isnan(temperature)) {
    Serial.println("Error when read data from DHT sensor!");
  }
}

void onLed() {
  // Set LED control pin
  pinMode(RED_PIN, OUTPUT);
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(BLUE_PIN, OUTPUT);
}

void setLedColor(int red, int green, int blue) {
  analogWrite(RED_PIN, red);
	analogWrite(GREEN_PIN, green);
	analogWrite(BLUE_PIN, blue);
}

void connectWifi() {
  WiFi.begin(WIFI_NAME, WIFI_PASSWORD);

  Serial.print("WIFI connecting");

  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nConnected to WiFi network with IP Address: " + WiFi.localIP().toString());
}

// void uploadDataToCloud(float temp, float humidity) {
//   String url = THINGSPEAK_SERVER_URL + "&field1=" + temp + "&field2=" + humidity;

//   HTTPClient http;
//   http.begin(url);

//   int responseCode = http.GET();
//   String responseBody = http.getString();

//   if (responseCode > 0) {
//     Serial.println("Uploaded data to ThinkSpeak. Status: " + String(responseCode));
//   } else {
//     Serial.println("Failed to upload data. Error code: " + String(responseCode));

//     if (responseBody.length() > 0) {
//       Serial.println("Response from server: " + responseBody);
//     }
//   }

//   http.end();
// }

void uploadDataToWebServer(float temp, float humidity) {
  DynamicJsonDocument doc(1024);
  doc["temperature"] = temp;
  doc["humidity"] = humidity;

  String jsonString;
  serializeJson(doc, jsonString); // Convert JSON object to String

  client.send(jsonString);
  Serial.println("Data sent: " + jsonString);
}

void getSocketConnection() {
  Serial.println("Begin connect to WebSocket server.");

  if (client.connect(WEBSOCKET_SERVER_URL)) {
    connectSocket = true;
    Serial.println("Connected to WebSocket server!");
  } else {
    connectSocket = false;
    Serial.println("Failed to connect to WebSocket server!");
    return;
  }

  client.onMessage([&](WebsocketsMessage message) {
    Serial.print("Received response from server: ");
    Serial.println(message.data());

    // Parse JSON to data from response
    DynamicJsonDocument doc(1024);
    DeserializationError error = deserializeJson(doc, message.data());

    if (error) {
      Serial.print("Error parsing JSON: ");
      Serial.println(error.c_str());
      return;
    }

    // Example response: {"success":"true", "message":"Hello", "action": "0"}
    const char* status = doc["success"];
    const char* messages = doc["message"];
    int action = doc["action"];

    Serial.println("Status: " + String(status));
    Serial.println("Messages: " + String(messages));
    Serial.println("Action: " + String(action));

    executeSocketRequest(action, messages);
  });

  client.ping();
}

void executeSocketRequest(int action, const char* messages) {
  switch (action) {
    case -1: // Response no action from server
      Serial.println(messages);
      break;

    case LED_ON:
      ledStatus = true;
      break;

    case LED_OFF:
      ledStatus = false;
      setLedColor(0, 0, 0);
      break;

    case LED_NOTIFY:
      if (ledStatus) {
        ledWarningStatus = true;
      }
      break;

    case LCD_ON:
      lcdStatus = true;
      break;

    case LCD_OFF:
      lcdStatus = false;
      tft.fillScreen(TFT_BLACK); // Clear LCD monitor
      break;

    case NOTIFY:
      if (lcdStatus) {
        lcdNotify(messages);
      }
      break;

    case CHANGE_REFRESH_TIME:
      timeRefreshData = atoi(messages);
      Serial.println("Change refresh time reading data of DHT sensor to: " + timeRefreshData);
      break;

    default:
      Serial.println("Invalid server execution request!");
  }
}

void setup() {
  Serial.begin(9600);
  startTime = millis();

  tft.init();
  tft.setRotation(3); // Rotate 90 deg
  tft.setTextColor(TFT_WHITE, TFT_BLACK); // Set display text color white, background black
  tft.setTextSize(1);
  tft.fillScreen(TFT_BLACK);

  dht.begin();
  connectWifi();

  onLed();
  getSocketConnection(); // Create socket connection and listen response from server
}

void loop() {
  float temperature, humidity;

  getTemperatureAndHumidity(temperature, humidity);

  if (millis() - startTime >= timeRefreshData) {
    startTime = millis();

    if (!isnan(humidity) && !isnan(temperature)) {
      // uploadDataToCloud(temperature, humidity);
      
      if (connectSocket) {
        uploadDataToWebServer(temperature, humidity);
      }
    }

    if (lcdStatus) {
      String temp, rh;

      if (isnan(temperature)) {
        temp = "NaN";
      } else {
        temp = String(temperature) + " ";
      }
      
      if (isnan(humidity)) {
        rh = "NaN";
      } else {
        rh = String(humidity) + " %";
      }

      char degreeSymbol[20];
      sprintf(degreeSymbol + strlen(degreeSymbol), "%c", 248);

      lcdDisplay("Temperature: ", 5, 10, true);
      lcdDisplay(temp, 100, 10, false);
      lcdDisplay(String(degreeSymbol), 134, 8, false);
      lcdDisplay("C", 139, 10, false);

      lcdDisplay("Humidity:", 5, 25, false);
      lcdDisplay(rh, 100, 25, false);

      lcdDisplay("Refresh: " + String(timeRefreshData) + " ms", 5, 40, false);
    }
  }

  if (ledWarningStatus && ledStatus) {
    ledWarningStatus = false;

    for (int i = 0; i < 20; ++i) {
      setLedColor(255, 0, 0);
      delay(100);
      setLedColor(0, 0, 0);
      delay(100);
    }
  }

  if (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("WiFi disconnected. Reconnecting...");
    connectWifi();
  }

  if (!connectSocket || !client.available()) {
    delay(1000);
    Serial.println("Socket connection is closed. Try to reconnect...");
    getSocketConnection(); // Try reconnecting to Websocket server
  }

  client.poll(); // Wait socket response from server
}
