package com.kheyma.cloud.service.impl;

import com.kheyma.cloud.service.CloudStorageService;
import org.springframework.stereotype.Service;

@Service
public class MockCloudStorageService implements CloudStorageService {

    @Override
    public String uploadFile(byte[] file, String fileName) {
        return "https://mock-cloud-service.com/files/" + fileName;
    }

    @Override
    public void deleteFile(String fileUrl) {
        System.out.println("Mock delete file: " + fileUrl);
    }
}
