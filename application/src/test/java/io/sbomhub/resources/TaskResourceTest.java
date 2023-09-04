package io.sbomhub.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.sbomhub.dto.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@TestMethodOrder(OrderAnnotation.class)
@QuarkusTest
public class TaskResourceTest {

//    static OrganizationDto organizationDto = new OrganizationDto(UUID.randomUUID().toString(), null);
//    static RepositoryDto repositoryDto = new RepositoryDto(
//            null,
//            RepositoryType.Git,
//            "https://github.com/windup/windup-operator/",
//            "quay.io/repository/image:latest",
//            null
//    );
//
//    @Test
//    public void createTask() {
//        organizationDto = given()
//                .contentType(ContentType.JSON)
//                .when().body(organizationDto).post("/products")
//                .then()
//                .statusCode(200)
//                .body(
//                        "name", is(organizationDto.name()),
//                        "description", is(nullValue())
//                )
//                .extract().body().as(OrganizationDto.class);
//        repositoryDto = given()
//                .contentType(ContentType.JSON)
//                .when().body(repositoryDto).post("/products/" + organizationDto.name() + "/repositories")
//                .then()
//                .statusCode(200)
//                .body(
//                        "id", is(notNullValue()),
//                        "url", is(repositoryDto.url())
//                )
//                .extract().body().as(RepositoryDto.class);
//
//        TaskDto taskDto = new TaskDto(
//                null,
//                "task-1",
//                null,
//                repositoryDto,
//                null
//        );
//
//        given()
//                .contentType(ContentType.JSON)
//                .when().body(taskDto).post("/tasks")
//                .then()
//                .statusCode(200)
//                .body(
//                        "id", is(notNullValue()),
//                        "state", is(TaskState.Created.toString())
//                );
//
////        await().atMost(60, TimeUnit.SECONDS).until(() -> {
////            return false;
////        });
//    }

}