package example.controller;

import example.service.UserService;
import example.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import java.io.IOException;

public class UserLoginHandler implements HttpHandler {
    private final UserService userService;

    public UserLoginHandler(UserService userService) {
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
            String username = request.getString("username");
            String password = request.getString("password");

            User user = userService.authenticate(username, password);
            if (user == null) {
                sendResponse(exchange, 401, "Invalid credentials");
                return;
            }

            String token = JwtUtil.generateToken(user.getUsername(), user.getRole());
            JSONObject response = new JSONObject();
            response.put("token", token);
            response.put("role", user.getRole());

            sendResponse(exchange, 200, response.toString());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Login failed");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}