package io.sbomhub.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import io.sbomhub.dto.*;
import io.sbomhub.resources.containers.MinioServer;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@TestMethodOrder(OrderAnnotation.class)
@QuarkusTest
@TestProfile(value = OrganizationsResourceTest.Profile.class)
public class OrganizationsResourceTest {

    static OrganizationDto organizationDto = new OrganizationDto(UUID.randomUUID().toString(), null);
    static RepositoryDto repositoryDto = new RepositoryDto(
            "trustification",
            "RepositoryType.Git"
    );

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

    @Test
    @Order(1)
    public void createOrganization() {
        given()
                .contentType(ContentType.JSON)
                .when().body(organizationDto).post("/organizations")
                .then()
                .statusCode(200)
                .body(
                        "name", is(organizationDto.name()),
                        "description", is(nullValue())
                );
    }

    @Test
    @Order(2)
    public void getOrganization() {
        given()
                .contentType(ContentType.JSON)
                .when().get("/organizations/" + organizationDto.name())
                .then()
                .body(
                        "name", is(organizationDto.name()),
                        "description", is(nullValue())
                );
    }

    @Test
    @Order(3)
    public void createRepository() {
        given()
                .contentType(ContentType.JSON)
                .when().body(repositoryDto).post("/organizations/" + organizationDto.name() + "/repositories")
                .then()
                .statusCode(200)
                .body(
                        "name", is(repositoryDto.name())
                )
                .extract().body().as(RepositoryDto.class);
    }

    @Test
    @Order(4)
    public void getRepositories() {
        given()
                .contentType(ContentType.JSON)
                .when().get("/organizations/" + organizationDto.name() + "/repositories")
                .then()
                .statusCode(200)
                .body(
                        "size()", is(1),
                        "[0].name", is(notNullValue())
                );
    }

    @Test
    @Order(5)
    public void createSbom() throws URISyntaxException {
        List<String> sboms = Arrays.asList(
                "sbom/mtr-1.0.json",
                "sbom/quarkus-2.7.json"
        );

        for (String sbomb : sboms) {
            URI fileURI = OrganizationsResourceTest.class.getClassLoader().getResource(sbomb).toURI();
            File file = new File(fileURI);

            given()
                    .contentType(ContentType.MULTIPART)
                    .multiPart("file", file)
                    .multiPart("tag", "1.0")
                    .when().post("/organizations/" + organizationDto.name() + "/repositories/" + repositoryDto.name() + "/sboms")
                    .then()
                    .statusCode(200)
                    .body(
                            "id", is(notNullValue()),
                            "status", is(SbomStatus.SCHEDULED.toString())
                    )
                    .extract().body().as(SbomDto.class);
        }
    }

    @Test
    @Order(6)
    public void getSboms() {
        Awaitility.await().atMost(5, TimeUnit.MINUTES).untilAsserted(() -> {
            given()
                    .contentType(ContentType.JSON)
                    .when().get("/organizations/" + organizationDto.name() + "/repositories/" + repositoryDto.name() + "/sboms")
                    .then()
                    .statusCode(200)
                    .body(
                            "size()", is(2),
                            "[0].status", is(SbomStatus.COMPLETED.toString()),
                            "[1].status", is(SbomStatus.COMPLETED.toString())
                    );
        });
    }

    @Test
    @Order(7)
    public void getPackages() throws InterruptedException {
        given()
                .contentType(ContentType.JSON)
                .when().get("/packages?sbom=1")
                .then()
                .body("meta.count", is(378));

        given()
                .contentType(ContentType.JSON)
                .when().get("/packages?sbom=2")
                .then()
                .statusCode(200)
                .body("meta.count", is(4796));

        given()
                .contentType(ContentType.JSON)
                .when().get("/packages")
                .then()
                .statusCode(200)
                .body("meta.count", is(5174));

        given()
                .contentType(ContentType.JSON)
                .when().get("/packages?q=antlr:antlr:jar")
                .then()
                .statusCode(200)
                .body("meta.count", is(1),
                        "data[0].count", is(2)
                );

        given()
                .contentType(ContentType.JSON)
                .when().get("/packages?q=antlr:antlr:jar&sbom=2")
                .then()
                .statusCode(200)
                .body("meta.count", is(1),
                        "data[0].count", is(2)
                );
    }

}