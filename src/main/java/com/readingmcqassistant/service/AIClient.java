package com.readingmcqassistant.service;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIClient {

 
    private static final String BASE_URL = "http://127.0.0.1:8081";  
    private static final String ENDPOINT = "/predict";               


 
    public Result predictRawPayload(String payloadJson) throws Exception {
        URL url = new URL(BASE_URL + ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(75_000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");


        byte[] bytes = payloadJson.getBytes(StandardCharsets.UTF_8);
        System.out.println("[AIClient] Sending JSON (" + bytes.length + " bytes) → " + url);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(bytes);
        }

        // Đọc phản hồi
        int status = conn.getResponseCode();
        InputStream is = (status / 100 == 2) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        String body = sb.toString();
        System.out.println("[AIClient] HTTP " + status + " response = " + body);

        if (status / 100 != 2) {
            throw new RuntimeException("Model API error HTTP " + status + ": " + body);
        }

        String letter  = extract(body, "\"answer_letter\"\\s*:\\s*\"([A-D])\"");
        String latency = extract(body, "\"latency_ms\"\\s*:\\s*(\\d+)");

        if (letter == null) {
            throw new RuntimeException("Missing answer_letter in response: " + body);
        }

        Result r = new Result();
        r.answerLetter = letter;
        r.latencyMs = (latency != null) ? Integer.parseInt(latency) : null;
        return r;
    }

    private static String extract(String s, String regex) {
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.find() ? m.group(1) : null;
    }

    public static class Result {
        public String answerLetter; 
        public Integer latencyMs;

        @Override
        public String toString() {
            return "Result{" +
                    "answerLetter='" + answerLetter + '\'' +
                    ", latencyMs=" + latencyMs +
                    '}';
        }
    }
}
