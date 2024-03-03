package org.acme.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BlobService {

    public static final String TIMESHEET_CONTAINER = "timesheetcontainer";
    public static final String NOTE_CONTAINER = "notecontainer";

    @ConfigProperty(name = "azure.storage.connection-string")
    private String connectionString;

    public List<String> getAllBlobFileNames(String container) {
        BlobServiceClient blobServiceClient =
                new BlobServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();
        return blobServiceClient.getBlobContainerClient(container == null ? NOTE_CONTAINER : container)
                .listBlobs().stream()
                .map(BlobItem::getName)
                .collect(Collectors.toList());
    }

    public void uploadToBlob(String fileName, InputStream fileContent) {
        BlobServiceClient blobServiceClient =
                new BlobServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();
        blobServiceClient.getBlobContainerClient(TIMESHEET_CONTAINER)
                .getBlobClient(fileName)
                .upload(fileContent, true);
    }

    public String getDownloadLinkFromBlob(String fileName) {
        BlobServiceClient blobServiceClient =
                new BlobServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();
        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1); // The SAS will be valid for 1 day
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions);
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(TIMESHEET_CONTAINER)
                .getBlobClient(fileName);
        String sasToken = blobClient.generateSas(sasValues);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }

}
