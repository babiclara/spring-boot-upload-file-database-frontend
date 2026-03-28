package com.example.springbootuploadfiledatabasefrontend.service;

import com.example.springbootuploadfiledatabasefrontend.model.FileCategory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class FileCategoryService {

    private static final String BASE = "http://localhost:8080/api/categories";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<FileCategory> getAllCategories() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("GET failed: HTTP " + response.statusCode());
        }

        FileCategory[] categories = objectMapper.readValue(
                response.body(), FileCategory[].class
        );
        return Arrays.asList(categories);
    }

    public FileCategory createCategory(FileCategory category) throws Exception {
        String json = objectMapper.writeValueAsString(category);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 201) {
            throw new RuntimeException("POST failed: HTTP " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), FileCategory.class);
    }

    public FileCategory updateCategory(Long id, FileCategory category) throws Exception {
        String json = objectMapper.writeValueAsString(category);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("PUT failed: HTTP " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), FileCategory.class);
    }

    public void deleteCategory(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 204) {
            throw new RuntimeException("DELETE failed: HTTP " + response.statusCode());
        }
    }
}