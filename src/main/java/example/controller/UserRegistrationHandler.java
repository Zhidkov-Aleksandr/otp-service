package example.controller;

import example.service.UserService;
import example.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import java.io.IOException;

public class UserRegistrationHandler implements HttpHandler {
    private final UserService userService;

    public UserRegistrationHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            JSONObject request = new JSONObject(new String(exchange.getRequestBody().readAllBytes()));
            String role = request.optString("role", "USER");

            userService.registerUser(
                    request.getString("username"),
                    request.getString("password"),
                    role
            );

            sendResponse(exchange, 201, "User registered successfully");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Registration failed");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}