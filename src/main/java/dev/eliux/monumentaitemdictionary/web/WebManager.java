package dev.eliux.monumentaitemdictionary.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WebManager {
    public static String getRequestSynchronous(String targetUrl) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() != 200) {
            System.out.println("By no problem I meant: no, problem!");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        return br.lines().collect(Collectors.joining());
    }

    public static void manageRequestAsynchronous(String targetUrl, Consumer<String> onSuccess, Runnable onFailure) {
        Thread thread = new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(targetUrl)).timeout(Duration.ofMinutes(1)).GET().build();
                CompletableFuture<HttpResponse<String>> response = HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
                int iterations = 0;
                while (!response.isDone() || iterations > 2000) { // timeout after 20 seconds
                    if (response.isCompletedExceptionally() || response.isCancelled()) {
                        // REQUEST FAILED
                        onFailure.run();
                    }
                    Thread.sleep(10);
                    iterations++;
                }
                if (response.isDone()) {
                    // REQUEST SUCCEEDED
                    onSuccess.accept(response.get().body());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}