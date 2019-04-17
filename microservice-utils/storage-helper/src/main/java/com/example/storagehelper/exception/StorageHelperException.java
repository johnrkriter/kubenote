package com.example.storagehelper.exception;

/**
 * @author c.horprasertwong
 */
public class StorageHelperException extends RuntimeException {
    public StorageHelperException() {
        super("Storage helper exception");
    }
    public StorageHelperException(String message) {
        super(message);
    }
}
