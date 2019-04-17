package com.example.storagehelper.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author c.horprasertwong
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class UploadedStorageObjectResult implements Serializable {
    private String bucketName;
    private String filePath;
    private String versionId;
    private String contentMd5;
    private Map<String, String> metadata;
    private Map<String, Object> rawMetadata;

    public UploadedStorageObjectResult(String bucketName, String filePath, String versionId, String contentMd5,
                                       Map<String, String> userMetadata, Map<String, Object> rawMetadata) {
        this.bucketName = bucketName;
        this.filePath = filePath;
        this.versionId = versionId;
        this.contentMd5 = contentMd5;
        this.metadata = userMetadata;
        this.rawMetadata = rawMetadata;
    }
}
