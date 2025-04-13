package example.service;

import org.opensmpp.*;
import org.opensmpp.pdu.*;
import java.util.Properties;

public class SmsNotificationService implements NotificationService {
    private final String host;
    private final int port;
    private final String systemId;
    private final String password;

    public SmsNotificationService() {
        Properties props = loadConfig();
        this.host = props.getProperty("smpp.host");
        this.port = Integer.parseInt(props.getProperty("smpp.port"));
        this.systemId = props.getProperty("smpp.system_id");
        this.password = props.getProperty("smpp.password");
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(SmsNotificationService.class.getClassLoader()
                    .getResourceAsStream("sms.properties"));
            return props;
        } catch (Exception e) {
            throw new NotificationException("Failed to load SMS config", e);
        }
    }

    @Override
    public void sendSms(String phoneNumber, String code) throws NotificationException {
        try {
            // SMPP подключение и отправка
            TCPIPConnection connection = new TCPIPConnection(host, port);
            Session session = new Session(connection);

            BindRequest bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);

            session.bind(bindRequest);

            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr("OTPService");
            submitSM.setDestAddr(phoneNumber);
            submitSM.setShortMessage("Your code: " + code);

            session.submit(submitSM);
            session.unbind();
        } catch (Exception e) {
            throw new NotificationException("SMS sending failed", e);
        }
    }
}