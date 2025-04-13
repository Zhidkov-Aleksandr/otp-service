package example.controller;

import example.service.OtpService;
import example.service.NotificationService;
import example.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import java.io.IOException;

public class OtpGenerateHandler implements HttpHandler {
    private final OtpService otpService;
    private final NotificationService notificationService;

    public OtpGenerateHandler(OtpService otpService, NotificationService notificationService) {
        this.otpService = otpService;
        this.notificationService = notificationService;
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
            String channel = request.getString("channel");
            String destination = request.getString("destination");

            // Получаем user ID из токена
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            Claims claims = JwtUtil.parseToken(authHeader.substring(7));
            int userId = claims.get("userId", Integer.class);

            // Генерируем OTP
            String code = otpService.generateOtp(userId, operationId);

            // Отправляем код
            switch (channel.toLowerCase()) {
                case "email":
                    notificationService.sendEmail(destination, code);
                    break;
                case "sms":
                    notificationService.sendSms(destination, code);
                    break;
                case "telegram":
                    notificationService.sendTelegram(destination, code);
                    break;
                case "file":
                    notificationService.saveToFile(code);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid channel");
            }

            JSONObject response = new JSONObject();
            response.put("operationId", operationId);
            response.put("status", "CODE_SENT");

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
