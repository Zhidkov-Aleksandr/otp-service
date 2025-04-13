package example.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailNotificationService implements NotificationService {
    private final Session session;
    private final String fromEmail;

    public EmailNotificationService() {
        Properties config = loadConfig();
        this.fromEmail = config.getProperty("email.username");
        String password = config.getProperty("email.password");

        this.session = Session.getInstance(config, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(EmailNotificationService.class.getClassLoader()
                    .getResourceAsStream("email.properties"));
            return props;
        } catch (Exception e) {
            throw new NotificationException("Failed to load email config", e);
        }
    }

    @Override
    public void sendEmail(String toEmail, String code) throws NotificationException {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Your OTP Code");
            message.setText("Your verification code is: " + code);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new NotificationException("Email sending failed", e);
        }
    }


    @Override
    public void sendSms(String phoneNumber, String code) {
        throw new UnsupportedOperationException("SMS not supported in Email service");
    }

}