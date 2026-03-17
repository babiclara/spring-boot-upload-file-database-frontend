package com.example.springbootuploadfiledatabasefrontend.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MultipartBuilder {

    public static byte[] build(String boundary, File file) throws IOException {
        String mime = Files.probeContentType(file.toPath());
        if (mime == null) mime = "application/octet-stream";

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] header = ("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n"
                + "Content-Type: " + mime + "\r\n\r\n").getBytes();
        byte[] footer = ("\r\n--" + boundary + "--\r\n").getBytes();

        byte[] body = new byte[header.length + fileBytes.length + footer.length];
        System.arraycopy(header, 0, body, 0, header.length);
        System.arraycopy(fileBytes, 0, body, header.length, fileBytes.length);
        System.arraycopy(footer, 0, body, header.length + fileBytes.length, footer.length);

        return body;
    }
}