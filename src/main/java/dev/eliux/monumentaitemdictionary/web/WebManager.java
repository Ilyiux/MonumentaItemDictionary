package dev.eliux.monumentaitemdictionary.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class WebManager {
    public static String getRequest(String targetUrl) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() != 200) {
            System.out.println("By no problem I meant: no, problem!");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String responseBody = br.lines().collect(Collectors.joining());

        return responseBody;
    }
}