package io.sbombhub.producer;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.sbombhub.producer.containers.MinioServer;
import io.sbombhub.producer.containers.SbomhubServer;
import jakarta.inject.Inject;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuarkusTest
@TestProfile(value = MainTest.Profile.class)
public class MainTest {

    @Inject
    ProducerTemplate producerTemplate;

    public static class Profile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("storage.type", "minio");
        }

        @Override
        public List<TestResourceEntry> testResources() {
            return List.of(
                    new TestResourceEntry(MinioServer.class),
                    new TestResourceEntry(SbomhubServer.class)
            );
        }
    }

    @Test
    public void testAnalysis(@TempDir Path tempPath) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("repository", tempPath.toString());

        producerTemplate.requestBodyAndHeaders("direct:analyze", null, headers);
    }
}