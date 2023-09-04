package io.sbomhub.resources.containers;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class S3Server implements QuarkusTestResourceLifecycleManager {

    public LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(LocalStackContainer.Service.S3);

    @Override
    public Map<String, String> start() {
        localstack.start();

        URI host = localstack.getEndpointOverride(LocalStackContainer.Service.S3);

        Map<String, String> properties = new HashMap<>();
        properties.put("storage.host", host.toString());
        properties.put("storage.region", localstack.getRegion());
        properties.put("storage.access_key_id", localstack.getAccessKey());
        properties.put("storage.secret_access_key", localstack.getSecretKey());

        return properties;
    }

    @Override
    public void stop() {
        localstack.stop();
    }

}
