package io.sbomhub.resources.storage;

import com.google.common.io.Files;
import jakarta.inject.Inject;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class AbstractStorageTest {

    @ConfigProperty(name = "storage.type")
    String storageType;

    @Inject
    ProducerTemplate producerTemplate;

    @Test
    public void uploadBytes() {
        // Given
        byte[] body = new byte[]{1, 2, 3};

        Map<String, Object> headers = new HashMap<>();
        headers.put("shouldZipFile", true);

        // When
        String fileId = producerTemplate.requestBodyAndHeaders("direct:" + storageType + "-save-file", body, headers, String.class);

        // Then
        assertNotNull(fileId);
    }

    @Test
    public void uploadFile(@TempDir Path tempPath) throws IOException {
        // Given
        String filename = "myfile.xml";
        byte[] fileContent = new byte[]{1, 2, 3};
        File body = tempPath.resolve(filename).toFile();
        Files.write(fileContent, body);

        // When
        Map<String, Object> headers = new HashMap<>();
        headers.put("shouldZipFile", true);

        String fileId = producerTemplate.requestBodyAndHeaders("direct:" + storageType + "-save-file", body, headers, String.class);

        // Then
        assertNotNull(fileId);
    }

}
