package example.dao;

import example.model.OtpCode;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

import example.model.OtpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpCodeDao {
    private static final Logger logger = LoggerFactory.getLogger(OtpCodeDao.class);
    private final Connection connection;
    private String status;

    public OtpCodeDao(Connection connection) {
        this.connection = connection;
    }

    public void setStatus(OtpStatus status) {
        this.status = status.toString();
    }


    public OtpStatus getStatus() {
        return OtpStatus.valueOf(status);
    }

    public void save(OtpCode otp) throws SQLException {
        String sql = "INSERT INTO otp_codes(user_id, code, status, created_at, expires_at, operation_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, otp.getUserId());
            stmt.setString(2, otp.getCode());
            stmt.setString(3, otp.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(otp.getCreatedAt()));
            stmt.setTimestamp(5, Timestamp.valueOf(otp.getExpiresAt()));
            stmt.setString(6, otp.getOperationId());
            stmt.executeUpdate();
        }
    }

    public Optional<OtpCode> findByCodeAndUserId(String code, int userId) throws SQLException {
        String sql = "SELECT * FROM otp_codes WHERE code = ? AND user_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OtpCode otp = new OtpCode();
                    otp.setId(rs.getInt("id"));
                    otp.setUserId(rs.getInt("user_id"));
                    otp.setCode(rs.getString("code"));
                    otp.setStatus(rs.getString("status"));
                    otp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    otp.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
                    otp.setOperationId(rs.getString("operation_id"));
                    return Optional.of(otp);
                }
            }
        }
        return Optional.empty();
    }

    public void updateStatus(int otpId, String status) throws SQLException {
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, otpId);
            stmt.executeUpdate();
        }
    }

    public void deleteExpiredCodes() throws SQLException {
        String sql = "DELETE FROM otp_codes WHERE expires_at < NOW()";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}