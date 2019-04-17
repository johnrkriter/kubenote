package com.example.storagehelper.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.core.io.Resource;

/**
 * @author c.horprasertwong
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalStorageObject extends StorageObject {
    private Resource resource;
    private InputStream inputStream;
    private String filePath;
    private String bucketName;
    private Map<String, String> metadata;
    private String md5;
    private String verionId;

    public LocalStorageObject(Resource resource, InputStream inputStream) {
        this.resource = resource;
        this.inputStream = inputStream;
    }

    @Override
    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public String getBucketName() {
        return this.bucketName;
    }

    @Override
    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    @Override
    public String getContentMd5() {
        return this.md5;
    }

    @Override
    public String getVersionId() {
        return this.verionId;
    }

    @Override
    public Object getRawObject() {
        return resource;
    }

    @Override
    public InputStream getContent() {
        return inputStream;
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            this.inputStream.close();
        }
    }
}
