package example.controller;

import com.example.dao.OtpConfigDao;
import com.example.model.OtpConfig;
import com.example.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import java.io.IOException;

public class AdminConfigHandler implements HttpHandler {
    private final OtpConfigDao otpConfigDao;

    public AdminConfigHandler(OtpConfigDao otpConfigDao) {
        this.otpConfigDao = otpConfigDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"PUT".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            // Authorization check
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendResponse(exchange, 401, "Unauthorized");
                return;
            }

            Claims claims = JwtUtil.parseToken(authHeader.substring(7));
            if (!"admin".equals(claims.get("role"))) {
                sendResponse(exchange, 403, "Forbidden");
                return;
            }

            // Process request
            JSONObject request = new JSONObject(new String(exchange.getRequestBody().readAllBytes()));
            OtpConfig config = new OtpConfig();
            config.setCodeLength(request.getInt("codeLength"));
            config.setExpirationMinutes(request.getInt("expirationMinutes"));

            otpConfigDao.updateConfig(config);
            sendResponse(exchange, 200, "Config updated");

        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}