package io.sbomhub.resources.storage;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.sbomhub.resources.containers.MinioServer;

import java.util.List;
import java.util.Map;

@QuarkusTest
@TestProfile(value = MinioRouteTest.Profile.class)
public class MinioRouteTest extends AbstractStorageTest {

    public static class Profile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("storage.type", "minio");
        }

        @Override
        public List<TestResourceEntry> testResources() {
            return List.of(new TestResourceEntry(MinioServer.class));
        }
    }

}