package example.service;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TelegramNotificationService implements NotificationService {
    private final String botToken;
    private final String chatId;
    private final String apiUrl;

    public TelegramNotificationService() {
        Properties props = loadConfig();
        this.botToken = props.getProperty("telegram.bot.token");
        this.chatId = props.getProperty("telegram.chat.id");
        this.apiUrl = props.getProperty("telegram.api.url") + botToken + "/sendMessage";
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(TelegramNotificationService.class.getClassLoader()
                    .getResourceAsStream("telegram.properties"));
            return props;
        } catch (Exception e) {
            throw new NotificationException("Failed to load Telegram config", e);
        }
    }

    @Override
    public void sendTelegram(String destination, String code) throws NotificationException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = URLEncoder.encode("Your code: " + code, StandardCharsets.UTF_8);
            String url = String.format("%s?chat_id=%s&text=%s", apiUrl, chatId, message);

            HttpGet request = new HttpGet(url);
            httpClient.execute(request);
        } catch (Exception e) {
            throw new NotificationException("Telegram sending failed", e);
        }
    }
}