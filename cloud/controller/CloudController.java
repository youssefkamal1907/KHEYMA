package com.kheyma.cloud.controller;

import com.kheyma.cloud.dto.CloudUploadResponse;
import com.kheyma.cloud.service.CloudStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cloud")
@RequiredArgsConstructor
public class CloudController {

    private final CloudStorageService cloudStorageService;

    @PostMapping("/upload")
    public CloudUploadResponse upload() {
        CloudUploadResponse response = new CloudUploadResponse();
        response.setFileUrl(
                cloudStorageService.uploadFile(new byte[]{}, "test.png")
        );
        return response;
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam String url) {
        cloudStorageService.deleteFile(url);
    }
}
