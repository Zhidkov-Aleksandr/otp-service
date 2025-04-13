package example.model;

public class OtpConfig {
    private int codeLength;
    private int expirationMinutes;

    public OtpConfig() {}

    public OtpConfig(int codeLength, int expirationMinutes) {
        this.codeLength = codeLength;
        this.expirationMinutes = expirationMinutes;
    }

    // Геттеры и сеттеры
    public int getCodeLength() { return codeLength; }
    public void setCodeLength(int codeLength) { this.codeLength = codeLength; }

    public int getExpirationMinutes() { return expirationMinutes; }
    public void setExpirationMinutes(int expirationMinutes) { this.expirationMinutes = expirationMinutes; }
}