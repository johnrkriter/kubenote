package com.example.storagehelper.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import com.example.storagehelper.config.StorageHelperConstants;
import com.example.storagehelper.config.minio.MinioConstants;
import com.example.storagehelper.exception.BucketNotFoundException;
import com.example.storagehelper.exception.FileNotFoundException;
import com.example.storagehelper.exception.StorageHelperException;
import com.example.storagehelper.model.StorageObject;
import com.example.storagehelper.model.UploadedStorageObjectResult;

import org.springframework.http.HttpHeaders;

/**
 * @author c.horprasertwong
 */
@Slf4j
public class MinioStorageHelper implements StorageHelper {
    private AmazonS3 s3Client;

    public MinioStorageHelper(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public UploadedStorageObjectResult upload(InputStream inputStream, String bucketName, String filePath,
                                              Map<String, String> metadata, boolean createBucketIfNotExist) {
        try {
            if (!s3Client.doesBucketExistV2(bucketName)) {
                log.debug("upload: bucket {} does not exist", bucketName);
                if (createBucketIfNotExist) {
                    log.debug("upload: create bucket {}", bucketName);
                    s3Client.createBucket(bucketName);
                } else {
                    throw new BucketNotFoundException("Bucket does not exist, please create bucket please upload");
                }
            }
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setUserMetadata(metadata);
            if (metadata != null && metadata.containsKey(HttpHeaders.CONTENT_LENGTH)) {
                objectMetadata.setContentLength(Long.parseLong(metadata.get(HttpHeaders.CONTENT_LENGTH)));
            }
            PutObjectResult result = s3Client.putObject(bucketName, filePath, inputStream, objectMetadata);
            log.debug("upload: file uploaded");
            return new UploadedStorageObjectResult(
                    bucketName, filePath, result.getVersionId(), result.getContentMd5(), result.getMetadata().getUserMetadata(), result.getMetadata().getRawMetadata()
            );
        } catch (AmazonServiceException ase) {
            log.error("upload: Exception from service {}", ase.getMessage(), ase);
        } catch (AmazonClientException ace) {
            log.error("upload: Exception from client {}", ace.getMessage(), ace);
        }
        throw new StorageHelperException();
    }

    @Override
    public UploadedStorageObjectResult upload(InputStream inputStream, String bucketName, String filePath, HttpHeaders httpHeaders, boolean createBucketIfNotExist) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(StorageHelperConstants.METADATA_UPDATED_BY_USER_ID, httpHeaders.getFirst("userid"));
        metadata.put(StorageHelperConstants.METADATA_API_CORRELATION_ID, httpHeaders.getFirst("correlationid"));
        metadata.put(StorageHelperConstants.METADATA_PROCESS_ID, httpHeaders.getFirst("processid"));
        return this.upload(inputStream, bucketName, filePath, metadata, createBucketIfNotExist);
    }

    /**
     * @param bucketName bucket name
     * @param filePath   key or full file path
     * @return
     */
    @Override
    public StorageObject download(String bucketName, String filePath) {
        try {
            return new StorageObject(s3Client.getObject(bucketName, filePath));
        } catch (AmazonServiceException ase) {
            log.error("download: Exception from service {}", ase.getMessage(), ase);
            if (ase.getErrorCode().equals(MinioConstants.FILE_NOT_FOUND_ERROR_CODE)) {
                throw new FileNotFoundException();
            } else if (ase.getErrorCode().equals(MinioConstants.BUCKET_NOT_FOUND_ERROR_CODE)) {
                throw new BucketNotFoundException();
            }
        } catch (AmazonClientException ace) {
            log.error("download: Exception from client {}", ace.getMessage(), ace);
        }
        throw new StorageHelperException();
    }

    @Override
    public void delete(String bucketName, String objectName) {
        log.debug("delete: bucketName={}, objectName={}", bucketName, objectName);
        try {
            s3Client.deleteObject(bucketName, objectName);
            log.debug("successfully removed {}/{}", bucketName, objectName);
        } catch (AmazonServiceException ase) {
            log.error("delete: Exception from service {}", ase.getMessage(), ase);
        } catch (AmazonClientException ace) {
            log.error("delete: Exception from client {}", ace.getMessage(), ace);
        }
    }

    @Override
    public void deleteDirectory(String bucketName, String path) {

        // List all files
        List<S3ObjectSummary> filesInDirectory = this.listAllInDirectory(bucketName, path);
        log.debug("Number of files to be deleted: {}", filesInDirectory.size());
        if (filesInDirectory.isEmpty()) {
            return;
        }

        // Delete files
        List<DeleteObjectsRequest.KeyVersion> keyVersionList = filesInDirectory.stream()
                .map(x -> new DeleteObjectsRequest.KeyVersion(x.getKey()))
                .collect(Collectors.toList());
        DeleteObjectsResult deleteObjectsResult = s3Client.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keyVersionList));
        log.debug("Number of files deleted {}", deleteObjectsResult.getDeletedObjects().size());
    }

    @Override
    public boolean doesFolderExist(String bucketName, String path) {
        ObjectListing objects = s3Client.listObjects(bucketName, path);
        return !(objects == null || objects.getObjectSummaries().isEmpty());
    }

    @Override
    public boolean doesFileExist(String bucketName, String objectName) {
        return s3Client.doesObjectExist(bucketName, objectName);
    }

    private List<S3ObjectSummary> listAllInDirectory(String bucketName, String path) {

        List<S3ObjectSummary> results = new ArrayList<>();

        // First query must be done with listObjects(), somehow.
        ObjectListing objects = s3Client.listObjects(bucketName, path);
        boolean noObjectFound = objects == null || objects.getObjectSummaries().isEmpty();
        if (noObjectFound) {
            log.warn("No object found from path {}", path);
            return results;
        }
        results.addAll(objects.getObjectSummaries());

        // Loop through until all files are listed.
        do {
            objects = s3Client.listNextBatchOfObjects(objects);
            results.addAll(objects.getObjectSummaries());
        } while (objects.isTruncated());
        return results;
    }

}
