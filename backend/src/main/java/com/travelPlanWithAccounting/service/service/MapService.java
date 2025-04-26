package com.travelPlanWithAccounting.service.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class MapService {

    @Value("${spring.google.apiKey}")
    private String apiKey;

    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    public JsonObject searchPlace(String query) throws Exception {
        String url = PLACES_SEARCH_URL + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&key=" + apiKey;
        return getJsonResponse(url);
    }

    public JsonObject geocode(String address) throws Exception {
        String url = GEOCODE_URL + "?address=" + URLEncoder.encode(address, StandardCharsets.UTF_8) + "&key=" + apiKey;
        System.err.println("url ="+url);
        return getJsonResponse(url);
    }

    public JsonObject reverseGeocode(double lat, double lng) throws Exception {
        String url = GEOCODE_URL + "?latlng=" + lat + "," + lng + "&key=" + apiKey;
        return getJsonResponse(url);
    }

    public JsonObject getDirections(String origin, String destination) throws Exception {
        String url = DIRECTIONS_URL + "?origin=" + URLEncoder.encode(origin, StandardCharsets.UTF_8) +
                "&destination=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
                "&key=" + apiKey;
        return getJsonResponse(url);
    }

    public JsonObject getDistanceMatrix(String origin, String destination) throws Exception {
        String url = DISTANCE_MATRIX_URL + "?origins=" + URLEncoder.encode(origin, StandardCharsets.UTF_8) +
                "&destinations=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
                "&key=" + apiKey;
        return getJsonResponse(url);
    }

    private JsonObject getJsonResponse(String urlStr) throws Exception {
        System.err.println("urlStr ="+urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int statusCode = conn.getResponseCode();
        if (statusCode != 200) {
            throw new RuntimeException("GCP API Error - HTTP Code: " + statusCode);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                response.append(line);
            }
            return JsonParser.parseString(response.toString()).getAsJsonObject();
        } finally {
            conn.disconnect();
        }
    }
}