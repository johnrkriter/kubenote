# Storage Helper (This documentation is outdated)
Helper for download/upload file to Storage-as-a-Service
- Support multiple storage
- Support finding server by Eureka

## Storage type = Minio

```properties
# Enable Storage-helper type Minio
# Can use true or 1 to enable
storage-helper.minio.enabled=true
# Use Eureka to find Minio service, default value = true
storage-helper.minio.use-eureka=
# Service name of Minio in Eureka (required if use-eureka is enabled)
storage-helper.minio.service-name=
# Endpoint where Minio server located (required if use-eureka is disabled)
storage-helper.minio.endpoint=

# Access key of Minio
storage-helper.minio.accessKey=

# Secret key of Minio
storage-helper.minio.secretKey=

# Default value = AWSS3V4SignerType
storage-helper.signer.override=

#Default value = ap-southeast-1
storage-helper.singing.region=
# Use MockStorageHelper (default value = false)
storage-helper.minio.use-mock=false
```

### Example
#### In application.properties
```properties
storage-helper.minio.enabled=true
storage-helper.minio.use-eureka=true
storage-helper.minio.service-name=MINIO
storage-helper.minio.accessKey=${minio.accesskey}
storage-helper.minio.secretKey=${minio.secretkey}
```

### Example when testing in local
```properties
storage-helper.minio.enabled=true
storage-helper.minio.use-eureka=false
storage-helper.minio.endpoint=https://minio1d.example.com:9000
storage-helper.minio.accessKey=XXXX
storage-helper.minio.secretKey=YYYY
```
### Example for Jenkins profile
```properties
minio.accesskey=XXXX
minio.secretkey=YYYY

storage-helper.minio.enabled=true
storage-helper.minio.use-mock=true
```
### Include in POM.xml
```xml
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>storage-helper</artifactId>
        </dependency>
```

## How to use

### Download
```java
storageHelper.download("example-bucket", "2018/06/25/test.pdf");
```
##### Caution
StorageHelper will throw RuntimeException

- <b>BucketNotFoundException</b> when bucket is not found
- <b>FileNotFoundException</b> when file path (key) is not exist
- <b>StorageHelperException</b> when StorageHelper cannot map error

### Upload
There are 2 methods that you can use to upload
```java
storageHelper.upload(
    uploadInputStream, destination.getBucketName(), destination.getFilePath(), metadata, true
);
```
You have to generate metadata by yourself 

And
```java
storageHelper.upload(
    uploadInputStream, destination.getBucketName(), destination.getFilePath(), httpHeaders, true
);
```
metadata is generated from httpHeaders
- "updated-by-user-id" from headers.userid
- "api-correlation-id" from headers.correlationid
- "api-process-id" from headers.processid

##### Caution
- <b>StorageHelperException</b> when StorageHelper cannot map error
