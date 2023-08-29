package io.sbombhub.producer.storage;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.sbombhub.producer.containers.S3Server;

import java.util.List;
import java.util.Map;

@QuarkusTest
@TestProfile(value = S3RouteTest.Profile.class)
public class S3RouteTest extends AbstractStorageTest {

    public static class Profile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("storage.type", "s3");
        }

        @Override
        public List<TestResourceEntry> testResources() {
            return List.of(new TestResourceEntry(S3Server.class));
        }
    }

}
