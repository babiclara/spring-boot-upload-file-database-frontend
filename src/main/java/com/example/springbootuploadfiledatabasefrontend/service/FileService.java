package com.example.springbootuploadfiledatabasefrontend.service;

import com.example.springbootuploadfiledatabasefrontend.model.ResponseFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class FileService {

    private static final String FILES_URL = "http://localhost:8080/files";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ResponseFile> getAllFiles() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FILES_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("failed: " + response.statusCode());
        }

        ResponseFile[] files = objectMapper.readValue(response.body(), ResponseFile[].class);
        return Arrays.asList(files);
    }

    public byte[] getById(String id) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FILES_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<byte[]> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new RuntimeException("failed: " + response.statusCode());
        }

        return response.body();
    }

    public String create() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/upload"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}