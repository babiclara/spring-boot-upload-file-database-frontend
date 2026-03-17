package com.example.springbootuploadfiledatabasefrontend.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

public class MultipartBuilder {

    public static byte[] build(String boundary, File file, String name) throws IOException {
        StringBuilder sb = new StringBuilder();

        if (name != null) {
            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"name\"\r\n\r\n");
            sb.append(name).append("\r\n");
        }

        byte[] fileBytes = new byte[0];
        if (file != null) {
            String mime = URLConnection.guessContentTypeFromName(file.getName());
            if (mime == null) mime = "application/octet-stream";
            fileBytes = Files.readAllBytes(file.toPath());

            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"\r\n");
            sb.append("Content-Type: ").append(mime).append("\r\n\r\n");
        }

        byte[] header = sb.toString().getBytes();
        byte[] footer = ("\r\n--" + boundary + "--\r\n").getBytes();

        byte[] body = new byte[header.length + fileBytes.length + footer.length];
        System.arraycopy(header, 0, body, 0, header.length);
        System.arraycopy(fileBytes, 0, body, header.length, fileBytes.length);
        System.arraycopy(footer, 0, body, header.length + fileBytes.length, footer.length);

        return body;
    }
}