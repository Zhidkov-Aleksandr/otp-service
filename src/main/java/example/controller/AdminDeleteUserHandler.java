package example.controller;

import com.example.dao.UserDao;
import com.example.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import java.io.IOException;

public class AdminDeleteUserHandler implements HttpHandler {
    private final UserDao userDao;

    public AdminDeleteUserHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"DELETE".equals(exchange.getRequestMethod())) {
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

            // Extract user ID from path
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            int userId = Integer.parseInt(parts[parts.length - 1]);

            userDao.deleteUser(userId);
            sendResponse(exchange, 200, "User deleted");

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid user ID");
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