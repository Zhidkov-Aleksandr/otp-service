package example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class FileNotificationService implements NotificationService {
    private static final String FILE_PATH = "otp_codes.txt";

    @Override
    public void saveToFile(String code) throws NotificationException {
        try {
            String content = LocalDateTime.now() + " | Code: " + code + "\n";
            Files.write(Paths.get(FILE_PATH),
                    content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new NotificationException("File write failed", e);
        }
    }

}