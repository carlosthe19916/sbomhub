package io.sbombhub.producer.containers;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpStatusCode.OK_200;

public class SbomhubServer implements QuarkusTestResourceLifecycleManager {

    private MockServerContainer sbomhubApi;

    @Override
    public Map<String, String> start() {
        sbomhubApi = new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.13.2"));
        sbomhubApi.start();

        new MockServerClient(sbomhubApi.getHost(), sbomhubApi.getServerPort())
                .when(request().withPath("/tasks/1"))
                .respond(response()
                        .withStatusCode(OK_200.code())
                        .withReasonPhrase(OK_200.reasonPhrase())
                );
        return new HashMap<>() {{
            put("sbomhub.url", sbomhubApi.getHost() + ":" + sbomhubApi.getServerPort());
        }};
    }

    @Override
    public void stop() {
        sbomhubApi.stop();
    }
}
