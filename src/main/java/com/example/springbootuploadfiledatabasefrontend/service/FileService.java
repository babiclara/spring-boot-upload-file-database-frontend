package com.example.springbootuploadfiledatabasefrontend.service;

import com.example.springbootuploadfiledatabasefrontend.model.ResponseFile;
import com.example.springbootuploadfiledatabasefrontend.utils.MultipartBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileService {

    private static final String BASE = "http://localhost:8080";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ResponseFile> getAllFiles() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/files"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("GET failed: " + response.statusCode());
        }

        ResponseFile[] files = objectMapper.readValue(response.body(), ResponseFile[].class);
        return Arrays.asList(files);
    }

    public String upload(File file) throws Exception {
        String boundary = UUID.randomUUID().toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/upload"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(MultipartBuilder.build(boundary, file, null)))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());        }

        return response.body();
    }

    public String update(String id, File file, String name) throws Exception {
        String boundary = UUID.randomUUID().toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/update/files/" + id))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(MultipartBuilder.build(boundary, file, name)))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("Update failed: " + response.statusCode());
        }

        return response.body();
    }

    public String delete(String id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/delete/files/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("Delete failed: " + response.statusCode());
        }

        return response.body();
    }
}