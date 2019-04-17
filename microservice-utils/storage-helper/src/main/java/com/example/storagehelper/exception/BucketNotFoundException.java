package com.example.storagehelper.exception;

/**
 * Common exception for bucket not found
 * @author c.horprasertwong
 */
public class BucketNotFoundException extends StorageHelperException {
    public BucketNotFoundException() {
        super("The specified bucket does not exist!");
    }

    public BucketNotFoundException(String message) {
        super(message);
    }
}
