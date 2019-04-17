package com.example.storagehelper.service;

import java.io.InputStream;
import java.util.Map;

import com.example.storagehelper.model.StorageObject;
import com.example.storagehelper.model.UploadedStorageObjectResult;

import org.springframework.http.HttpHeaders;

/**
 * @author c.horprasertwong
 */
public interface StorageHelper {
    UploadedStorageObjectResult upload(InputStream documentFileByteArray,
			String bucketName,
			String filePath,
			Map<String, String> metadata,
			boolean createBucketIfNotExist
	);

    UploadedStorageObjectResult upload(InputStream documentFileByteArray,
			String bucketName,
			String filePath,
			HttpHeaders httpHeaders,
			boolean createBucketIfNotExist
	);
    StorageObject download(String bucketName, String filePath);

    void delete(String bucketName, String objectName);

    void deleteDirectory(String bucketName, String path);

    boolean doesFolderExist(String bucketName, String objectName);

    boolean doesFileExist(String bucketName, String objectName);
}
