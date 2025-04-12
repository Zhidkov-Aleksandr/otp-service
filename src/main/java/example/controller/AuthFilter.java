package example.controller;

import com.example.util.JwtUtil;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import io.jsonwebtoken.Claims;
import java.io.IOException;

public class AuthFilter extends Filter {
    private final String[] publicRoutes = {"/register", "/login"};

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // Пропускаем публичные маршруты
        for (String route : publicRoutes) {
            if (path.equals(route)) {
                chain.doFilter(exchange);
                return;
            }
        }

        // Проверка токена для защищенных маршрутов
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(exchange, 401, "Missing authorization header");
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = JwtUtil.parseToken(token);
            exchange.setAttribute("userRole", claims.get("role"));
            exchange.setAttribute("username", claims.getSubject());
            chain.doFilter(exchange);
        } catch (Exception e) {
            sendErrorResponse(exchange, 401, "Invalid token");
        }
    }

    private void sendErrorResponse(HttpExchange exchange, int code, String message) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, message.length());
        exchange.getResponseBody().write(message.getBytes());
        exchange.close();
    }

    @Override
    public String description() {
        return "Authentication Filter";
    }
}