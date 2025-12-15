package com.kheyma.cloud.service;

public interface CloudStorageService {
    String uploadFile(byte[] file, String fileName);
    void deleteFile(String fileUrl);
}
