package Test;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class ConcurrentTest {
    private static final int USER_COUNT = 10;
    // URL Servlet thực tế trong Tomcat
    private static final String URL = "http://localhost:8080/reading-mcq-assistant/reading";

    public static void main(String[] args) throws InterruptedException {
        // JSON gửi tới servlet -> servlet gọi sang model
        String payloadJson = """
        {
          "passage": "Energy First is one of the UK’s most innovative energy suppliers. We were the first energy supplier in the country to offer smart meters free of charge to our customers. These computerized meters submit automatic electricity readings for once an hour and once daily for gas. This information goes directly to the customer’s online account, allowing them to view and monitor energy usage. By understanding how much money they are spending on energy, we strongly believe that people can take control of how much energy they use and make significant savings to their monthly bills. If you want to benefit from our smart meters, all you have to do is make us your energy supplier. Switching is simple. The first step is to click the ‘show prices’ button below to compare our tariff with that of your current provider. If you decide to go ahead, apply using our simple online form. We’ll handle the rest, and keep you regularly updated with the progress.. There’s no need to contact your current supplier. If, within 7 days of submitting your form, you change your mind about switching energy suppliers, don’t worry. We give you a 7-day cooling-off period during which you can cancel your application with no penalty. It will take approximately 5 weeks for us to complete the process of transferring you to our supply. A week before your supply goes live, we will email you to confirm a start date. Once you have become a customer, you’ll be contacted over the phone by a local installer to arrange a convenient time to fit your smart meters. You will need to be at home when these are fitted. Once they are installed, you can check your energy use online. Until then, you can submit monthly readings online in order to obtain an accurate bill.",
          "question": "What is the purpose of smart meters?",
          "options": [
            "to reduce the costs of energy bills",
            "to inform customers which suppliers offer the best rates",
            "to allow customers to pay their bills online",
            "to show customers how much energy they use"
          ]
        }
        """;

        CountDownLatch latch = new CountDownLatch(USER_COUNT);
        long globalStart = System.currentTimeMillis();

        for (int i = 1; i <= USER_COUNT; i++) {
            final int userId = i;
            new Thread(() -> {
                try {
                    long start = System.currentTimeMillis();
                    HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(payloadJson.getBytes(StandardCharsets.UTF_8));
                    }

                    int code = conn.getResponseCode();

                    String response;
                    try (InputStream is = (code >= 200 && code < 300)
                            ? conn.getInputStream()
                            : conn.getErrorStream()) {
                        response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    }

                    long duration = System.currentTimeMillis() - start;
                    System.out.printf("User %02d -> HTTP %d, %d ms, resp=%s%n",
                            userId, code, duration, shorten(response, 80));

                } catch (Exception e) {
                    System.err.println("User " + userId + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        long total = System.currentTimeMillis() - globalStart;
        System.out.printf("✅ All %d requests done in %.2f seconds%n", USER_COUNT, total / 1000.0);
    }

    // Giúp in response ngắn gọn
    private static String shorten(String s, int maxLen) {
        if (s == null) return "null";
        s = s.replaceAll("\\s+", " ");
        return s.length() > maxLen ? s.substring(0, maxLen) + "..." : s;
    }
}
