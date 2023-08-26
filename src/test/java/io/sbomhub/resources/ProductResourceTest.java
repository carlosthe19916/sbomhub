package io.sbomhub.resources;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.sbomhub.dto.GitRepositoryDto;
import io.sbomhub.dto.GitTaskDto;
import io.sbomhub.dto.ProductDto;
import io.sbomhub.models.TaskState;
import io.sbomhub.models.jpa.entity.GitRepositoryEntity;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;

@TestMethodOrder(OrderAnnotation.class)
@QuarkusTest
@TestHTTPEndpoint(ProductResource.class)
public class ProductResourceTest {

    static ProductDto productDto = new ProductDto("rhel", null);
    static GitRepositoryDto gitRepositoryDto = new GitRepositoryDto(
            null,
            "https://github.com/project-openubl/openubl",
            null,
            null
    );

    @Test
    @Order(1)
    public void createProduct() {
        given()
                .contentType(ContentType.JSON)
                .when().body(productDto).post("/")
                .then()
                .statusCode(200)
                .body(
                        "name", is(productDto.name()),
                        "description", is(nullValue())
                );
    }

    @Test
    @Order(2)
    public void createAndGetById() {
        given()
                .contentType(ContentType.JSON)
                .when().get("/" + productDto.name())
                .then()
                .body(
                        "name", is(productDto.name()),
                        "description", is(nullValue())
                );
    }

    @Test
    @Order(3)
    public void createRepository() {
        gitRepositoryDto = given()
                .contentType(ContentType.JSON)
                .when().body(gitRepositoryDto).post("/" + productDto.name() + "/repositories")
                .then()
                .statusCode(200)
                .body(
                        "id", is(notNullValue()),
                        "url", is(gitRepositoryDto.url())
                )
                .extract().body().as(GitRepositoryDto.class);
    }

    @Test
    @Order(4)
    public void getRepository() {
        given()
                .contentType(ContentType.JSON)
                .when().get("/" + productDto.name() + "/repositories")
                .then()
                .statusCode(200)
                .body(
                        "size()", is(1),
                        "[0].url", is(notNullValue())
                );
    }

    @Test
    @Order(5)
    public void createGitTask() {
        GitTaskDto gitTaskDto = new GitTaskDto(
                null,
                TaskState.Created
        );

        given()
                .contentType(ContentType.JSON)
                .when().body(gitTaskDto).post("/" + productDto.name() + "/repositories/" + gitRepositoryDto.id() + "/tasks")
                .then()
                .statusCode(200)
                .body(
                        "id", is(notNullValue()),
                        "state", is(TaskState.Created.toString())
                );

        await().atMost(60, TimeUnit.SECONDS).until(() -> {
            return false;
        });

    }
}