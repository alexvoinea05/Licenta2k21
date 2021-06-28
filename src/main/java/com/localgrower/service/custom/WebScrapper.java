package com.localgrower.service.custom;

import java.io.*;
import java.net.*;

public class WebScrapper {

    private String url;

    public WebScrapper(String url) {
        this.url = url;
    }

    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (var reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null;) {
                result.append(line);
            }
        }
        return result.toString();
    }

    public String getData(String FieldStart, String FieldEnd) {
        try {
            String html = getHTML(this.url);

            if (html == null) return null;

            String[] firstSplit = html.split(FieldStart);

            if (firstSplit == null || firstSplit.length < 2) return null;

            String[] secondSplit = firstSplit[1].split(FieldEnd);

            if (secondSplit == null || firstSplit.length < 1) return null;

            return secondSplit[0];
        } catch (Exception e) {
            return null;
        }
    }
}
