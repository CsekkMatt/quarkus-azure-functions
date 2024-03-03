package org.acme.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import jakarta.inject.Inject;
import org.acme.service.BlobService;

import java.util.Optional;

public class BlobFunction {

    @Inject
    private BlobService blobService;

    @FunctionName("GetAllBlobs")
    public HttpResponseMessage getAllNotes(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET}, route = "blobs", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        String container = request.getQueryParameters().get("container");
        context.getLogger().info("Getting all notes.");
        return request.createResponseBuilder(HttpStatus.OK).body(blobService.getAllBlobFileNames(container)).build();
    }

    public HttpResponseMessage uploadBlob(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST}, route = "blobs", authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Uploading a blob.");
        return request.createResponseBuilder(HttpStatus.OK).body("Blob uploaded").build();
    }

}
