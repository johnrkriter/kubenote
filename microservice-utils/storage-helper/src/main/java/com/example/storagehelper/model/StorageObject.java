package com.example.storagehelper.model;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

/**
 * @author c.horprasertwong
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class StorageObject implements Closeable, Serializable {
    private S3Object s3Object;

    public StorageObject(S3Object s3Object) {
        this.s3Object = s3Object;
    }

    public String getFilePath() {
        return this.s3Object.getKey();
    }

    public String getBucketName() {
        return this.s3Object.getBucketName();
    }

    public Map<String, String> getMetadata() {
        return this.s3Object.getObjectMetadata().getUserMetadata();
    }

    public String getContentMd5() {
        return this.s3Object.getObjectMetadata().getContentMD5();
    }

    public String getVersionId() {
        return this.s3Object.getObjectMetadata().getVersionId();
    }

    @JsonIgnore
    public Object getRawObject() {
        return this.s3Object;
    }

    @JsonIgnore
    public InputStream getContent() {
        return this.s3Object.getObjectContent();
    }


    @Override
    public void close() throws IOException {
        s3Object.close();
    }
}
