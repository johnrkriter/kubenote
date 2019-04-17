package com.example.storagehelper.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import com.example.storagehelper.model.StorageObject;
import com.example.storagehelper.model.UploadedStorageObjectResult;

import org.springframework.http.HttpHeaders;

/**
 * @author c.horprasertwong
 */
@Slf4j
public class MockStorageHelper implements StorageHelper {
    @Override
    public UploadedStorageObjectResult upload(InputStream documentFileByteArray, String bucketName, String filePath, Map<String, String> metadata, boolean createBucketIfNotExist) {
        log.info("You're using MockStorageHelper");
        return new UploadedStorageObjectResult("mock", "mock", "0", "1621bbbf-7e66-4700-8504-05bc05438c8e", new HashMap<>(), new HashMap<>());
    }

    @Override
    public UploadedStorageObjectResult upload(InputStream documentFileByteArray, String bucketName, String filePath, HttpHeaders httpHeaders, boolean createBucketIfNotExist) {
        log.info("You're using MockStorageHelper");
        return new UploadedStorageObjectResult("mock", "mock", "0", "1621bbbf-7e66-4700-8504-05bc05438c8e", new HashMap<>(), new HashMap<>());
    }

    @Override
    public StorageObject download(String bucketName, String filePath) {
        log.info("You're using MockStorageHelper");
        return new StorageObject(null);
    }

    @Override
    public void delete(String bucketName, String objectName) {
        log.info("You're using MockStorageHelper");
    }

    @Override
    public void deleteDirectory(String bucketName, String path) {log.info("You're using MockStorageHelper");}

    @Override
    public boolean doesFolderExist(String bucketName, String path) {log.info("You're using MockStorageHelper"); return false;}

    @Override
    public boolean doesFileExist(String bucketName, String objectName) {log.info("You're using MockStorageHelper"); return false;}
}
