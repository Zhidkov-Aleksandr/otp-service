package example.service;

public interface NotificationService {
    void sendEmail(String toEmail, String code) throws NotificationException;
    void sendSms(String phoneNumber, String code) throws NotificationException;
    void sendTelegram(String chatId, String code) throws NotificationException;
    void saveToFile(String code) throws NotificationException;
}

