package example.dao;

import example.model.OtpConfig;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpConfigDao {
    private static final Logger logger = LoggerFactory.getLogger(OtpConfigDao.class);
    private final Connection connection;

    public OtpConfigDao(Connection connection) {
        this.connection = connection;
    }

    public OtpConfig getConfig() throws SQLException {
        String sql = "SELECT * FROM otp_config WHERE id = 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                OtpConfig config = new OtpConfig();
                config.setCodeLength(rs.getInt("code_length"));
                config.setExpirationMinutes(rs.getInt("expiration_minutes"));
                return config;
            }
            throw new SQLException("OTP config not found");
        }
    }

    public void updateConfig(OtpConfig config) throws SQLException {
        String sql = "UPDATE otp_config SET code_length = ?, expiration_minutes = ? WHERE id = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, config.getCodeLength());
            stmt.setInt(2, config.getExpirationMinutes());
            stmt.executeUpdate();
        }
    }
}