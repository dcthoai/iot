package vn.ptit.dto.response;

import org.springframework.http.ResponseEntity;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;

public class ResponseJSON {

    public static ResponseEntity<?> ok(String message) {
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok().body(response.toString());
    }

    public static ResponseEntity<?> ok(String message, Object data) {
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.ok().body(response.toString());
    }

    public static ResponseEntity<?> badRequest(String message) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response.toString());
    }

    public static ResponseEntity<?> notFound(String message) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.toString());
    }

    public static ResponseEntity<?> serverError(String message) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
    }

    public static ResponseEntity<?> unAuthorized(String message) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.toString());
    }

    public static ResponseEntity<?> accessDenied(String message) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.toString());
    }

    public static String toJson(Boolean status, String message) {
        JSONObject response = new JSONObject();
        response.put("success", status);
        response.put("message", message);
        return response.toString();
    }

    public static String socketOk(String message, Object action) {
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", message);
        response.put("action", action);
        return response.toString();
    }

    public static String socketError(String message) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("message", message);
        response.put("action", "-1");
        return response.toString();
    }
}
