package example.controller;

import example.dao.UserDao;
import example.model.User;
import example.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class AdminUsersHandler implements HttpHandler {
    private final UserDao userDao;

    public AdminUsersHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equals(exchange.getRequestMethod())) {
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

            // Get all non-admin users
            List<User> users = userDao.findAllNonAdminUsers();
            JSONArray response = new JSONArray();
            for (User user : users) {
                JSONObject userJson = new JSONObject();
                userJson.put("id", user.getId());
                userJson.put("username", user.getUsername());
                userJson.put("role", user.getRole());
                response.put(userJson);
            }

            sendResponse(exchange, 200, response.toString());

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