package example.controller;

import com.example.service.OtpService;
import com.example.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import java.io.IOException;

public class OtpValidateHandler implements HttpHandler {
    private final OtpService otpService;

    public OtpValidateHandler(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            JSONObject request = new JSONObject(new String(exchange.getRequestBody().readAllBytes()));
            String operationId = request.getString("operationId");
            String code = request.getString("code");

            // Получаем user ID из токена
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            Claims claims = JwtUtil.parseToken(authHeader.substring(7));
            int userId = claims.get("userId", Integer.class);

            // Проверяем код
            boolean isValid = otpService.validateOtp(userId, operationId, code);

            JSONObject response = new JSONObject();
            response.put("operationId", operationId);
            response.put("status", isValid ? "VERIFIED" : "INVALID");

            sendResponse(exchange, 200, response.toString());
        } catch (Exception e) {
            sendResponse(exchange, 400, e.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}