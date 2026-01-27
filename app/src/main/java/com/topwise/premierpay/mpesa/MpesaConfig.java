package com.topwise.premierpay.mpesa;

public class MpesaConfig {
    // 1. YOUR APP KEYS (Keep these as they are from your Developer Portal)
    public static final String CONSUMER_KEY = "UCs3teklM8Anjl9VxDpZb4LZQkmNP8ACDnxdqCzTpeRzOv8e";
    public static final String CONSUMER_SECRET = "Rbd5wi8dTGDhXEVKx1nBT6l6qZE3g2FeN2H4dDigBqh1bhEuR8IVrvsLXJkX84Jj";

    // 2. SANDBOX BUSINESS SHORTCODE
    public static final String BUSINESS_SHORT_CODE = "174379";

    // 3. THE CORRECT PASSKEY (Do not change this for Sandbox!)
    // This is the raw key starting with "bfb2..."
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";

    // Toggle between Sandbox and Production
    private static final boolean IS_SANDBOX = true;

    public static String getBaseUrl() {
        return IS_SANDBOX ? "https://sandbox.safaricom.co.ke" : "https://api.safaricom.co.ke";
    }
}