package com.example.storagehelper.exception;

/**
 * Common exception for key not found
 * @author c.horprasertwong
 */
public class FileNotFoundException extends StorageHelperException {
    public FileNotFoundException() {
        super("File not found in storage, please check file path or file name");
    }
}
