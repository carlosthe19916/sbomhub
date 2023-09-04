package io.sbomhub.resources.containers;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.HashMap;
import java.util.Map;

public class MinioServer implements QuarkusTestResourceLifecycleManager {

    private GenericContainer<?> minio;

    @Override
    public Map<String, String> start() {
        minio = new GenericContainer<>("quay.io/minio/minio:latest")
                .withExposedPorts(9000)
                .withEnv("MINIO_ACCESS_KEY", "BQA2GEXO711FVBVXDWKM")
                .withEnv("MINIO_SECRET_KEY", "uvgz3LCwWM3e400cDkQIH/y1Y4xgU4iV91CwFSPC")
                .withCommand("server /data")
                .waitingFor(Wait.forHttp("/minio/health/live"));
        minio.start();

        String host = minio.getHost();
        Integer port = minio.getMappedPort(9000);

        Map<String, String> properties = new HashMap<>();
        properties.put("storage.host", "http://" + host + ":" + port);
        properties.put("storage.health.url", "http://" + host + ":" + port + "/minio/health/live");
        properties.put("storage.access_key_id", "BQA2GEXO711FVBVXDWKM");
        properties.put("storage.secret_access_key", "uvgz3LCwWM3e400cDkQIH/y1Y4xgU4iV91CwFSPC");

        return properties;
    }

    @Override
    public void stop() {
        minio.stop();
    }

}
