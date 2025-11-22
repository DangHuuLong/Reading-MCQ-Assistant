package Test;

import org.junit.jupiter.api.Test;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LoadTestReadingServlet {

    private static final String BASE = "http://localhost:8080/reading-mcq-assistant";
    private static final String LOGIN = BASE + "/login";
    private static final String READING = BASE + "/reading";
    private static final String STATUS = BASE + "/reading/status";

    private static final int JOB_COUNT = 50;

    record Metric(
            int index,
            long jobId,
            long sendTime,
            long receiveTime
    ) {
        long latency() { return receiveTime - sendTime; }
    }

    @Test
    public void fullFlowTest() throws Exception {

        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        login(client);

        List<Metric> metrics = submitJobs(client);

        pollJobs(client, metrics);

        printSummary(metrics);
    }

    // ============================================================
    // 1) LOGIN
    // ============================================================
    private void login(HttpClient client) throws Exception {
        System.out.println("\n=== STEP 1: LOGIN ===");

        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("username=dav&password=123"))
                .build();

        HttpResponse<String> resp =
                client.send(loginReq, HttpResponse.BodyHandlers.ofString());

        System.out.println("Login status = " + resp.statusCode());
        System.out.println("Login body = " + resp.body());

        if (resp.statusCode() != 200)
            throw new RuntimeException("❌ Login failed!");
    }

    // ============================================================
    // 2) SUBMIT JOBS CONCURRENTLY
    // ============================================================
    private List<Metric> submitJobs(HttpClient client) {

        System.out.println("\n=== STEP 2: SUBMIT " + JOB_COUNT + " JOBS (CONCURRENT) ===");
        List<CompletableFuture<Metric>> futures = new ArrayList<>();

        String passage = """
                The Amazon rainforest is facing increasing danger due to human activities.
                Over the past decades, large areas of the forest have been cleared for agriculture,
                cattle farming, and commercial logging. These activities destroy habitats and
                contribute heavily to climate change.
                """;

        String question = "What is the main threat to the Amazon rainforest?";
        String optionA = "Natural climate cycles";
        String optionB = "Human activities such as logging and farming";
        String optionC = "Lack of rainfall";
        String optionD = "Animal overpopulation";

        for (int i = 0; i < JOB_COUNT; i++) {

            final int index = i;   // 🔥 biến final để dùng trong lambda

            String form = "passage=" + encode(passage) +
                    "&question=" + encode(question) +
                    "&optionA=" + encode(optionA) +
                    "&optionB=" + encode(optionB) +
                    "&optionC=" + encode(optionC) +
                    "&optionD=" + encode(optionD);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(READING))
                    .header("X-Test", "true")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();

            long sendTime = System.currentTimeMillis();

            CompletableFuture<Metric> future =
                    client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                            .thenApply(resp -> {
                                long jobId = Long.parseLong(resp.body().replaceAll("\\D+", ""));
                                System.out.println("🟢 Submitted job #" + index + " → jobId = " + jobId);
                                return new Metric(index, jobId, sendTime, System.currentTimeMillis());
                            });

            futures.add(future);
        }


        return futures.stream().map(CompletableFuture::join).toList();
    }

    // ============================================================
    // 3) POLL JOB STATUS
    // ============================================================
    private void pollJobs(HttpClient client, List<Metric> metrics) throws Exception {

        System.out.println("\n=== STEP 3: POLLING STATUS FOR ALL JOBS ===");

        for (Metric m : metrics) {
            pollOne(client, m.jobId());
        }
    }

    private void pollOne(HttpClient client, long jobId) throws Exception {
        long start = System.currentTimeMillis();
        long timeout = 20_000;

        while (true) {

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(STATUS + "?job_id=" + jobId))
                    .GET()
                    .build();

            HttpResponse<String> resp =
                    client.send(req, HttpResponse.BodyHandlers.ofString());

            String json = resp.body();

            System.out.println("[Job " + jobId + "] → " + json);

            if (json.contains("\"SUCCEEDED\""))
                return;

            if (json.contains("\"FAILED\""))
                throw new RuntimeException("❌ Job " + jobId + " FAILED: " + json);

            if (System.currentTimeMillis() - start > timeout)
                throw new RuntimeException("⏳ Timeout waiting for job " + jobId);

            Thread.sleep(250);
        }
    }

    // ============================================================
    // 4) SUMMARY REPORT
    // ============================================================
    private void printSummary(List<Metric> metrics) {

        System.out.println("\n=== STEP 4: SUMMARY REPORT ===");

        long min = metrics.stream().mapToLong(Metric::latency).min().orElse(0);
        long max = metrics.stream().mapToLong(Metric::latency).max().orElse(0);
        double avg = metrics.stream().mapToLong(Metric::latency).average().orElse(0);

        System.out.println("Total submitted jobs = " + metrics.size());
        System.out.println("Min submission latency = " + min + " ms");
        System.out.println("Max submission latency = " + max + " ms");
        System.out.println("Avg submission latency = " + String.format("%.2f ms", avg));

        System.out.println("\nDetailed per-request:");
        for (Metric m : metrics) {
            System.out.printf(
                    "Req #%02d → jobId=%d | latency=%d ms\n",
                    m.index(), m.jobId(), m.latency()
            );
        }
    }

    // Helper
    private static String encode(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
