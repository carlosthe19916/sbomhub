package io.sbomhub.resources;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.sbomhub.dto.RepositoryDto;
import io.sbomhub.dto.ProductDto;
import io.sbomhub.dto.RepositoryType;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@TestMethodOrder(OrderAnnotation.class)
@QuarkusTest
@TestHTTPEndpoint(ProductResource.class)
public class ProductResourceTest {

    static ProductDto productDto = new ProductDto(UUID.randomUUID().toString(), null);
    static RepositoryDto repositoryDto = new RepositoryDto(
            null,
            RepositoryType.Git,
            "https://github.com/windup/windup-operator/",
            "quay.io/repository/image:latest",
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
        repositoryDto = given()
                .contentType(ContentType.JSON)
                .when().body(repositoryDto).post("/" + productDto.name() + "/repositories")
                .then()
                .statusCode(200)
                .body(
                        "id", is(notNullValue()),
                        "url", is(repositoryDto.url())
                )
                .extract().body().as(RepositoryDto.class);
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

}