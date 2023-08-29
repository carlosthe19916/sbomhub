package io.sbomhub.resources;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.sbomhub.dto.TaskDto;
import io.sbomhub.dto.TaskState;
import io.sbomhub.mapper.TaskMapper;
import io.sbomhub.models.jpa.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@ApplicationScoped
@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    TaskMapper taskMapper;

    @Inject
    Event<TaskDto> taskEvent;

    @Transactional(Transactional.TxType.NEVER)
    @POST
    @Path("/")
    public RestResponse<TaskDto> createTask(TaskDto taskDto) {
        if (taskDto.repository() == null) {
            return RestResponse.ResponseBuilder
                    .<TaskDto>create(RestResponse.Status.BAD_REQUEST)
                    .build();
        }

        QuarkusTransaction.begin();

        ProductEntity productEntity = null;
        RepositoryEntity repositoryEntity = RepositoryEntity.findById(taskDto.repository().id());
        if (repositoryEntity != null) {
            productEntity = repositoryEntity.product;
        }

        if (productEntity == null) {
            QuarkusTransaction.commit();
            return RestResponse.ResponseBuilder
                    .<TaskDto>create(RestResponse.Status.NOT_FOUND)
                    .build();
        }

        TaskEntity gitTaskEntity = new TaskEntity();
        gitTaskEntity.name = "task";
        gitTaskEntity.state = TaskState.Created;
        gitTaskEntity.repository = repositoryEntity;
        gitTaskEntity.persist();

        gitTaskEntity.name = "task-" + gitTaskEntity.id + "-" + UUID.randomUUID().toString();
        gitTaskEntity.persist();

        TaskDto result = taskMapper.toDto(gitTaskEntity);
        QuarkusTransaction.commit();

        taskEvent.fire(result);
        return RestResponse.ResponseBuilder
                .<TaskDto>create(RestResponse.Status.OK)
                .entity(result)
                .build();
    }

    @PUT
    @Path("/{taskId}")
    public RestResponse<TaskDto> updateTask(@PathParam("taskId") Long taskId, TaskDto taskDto) {
        return TaskEntity.<TaskEntity>findByIdOptional(taskId)
                .map(taskEntity -> {
                    taskEntity.state = taskDto.state();
                    if (Objects.requireNonNull(taskDto.state()) == TaskState.Succeeded) {
                        taskEntity.sboms = taskDto.sbombs().stream()
                                .map(sbomDto -> {
                                    SbomEntity sbomEntity = new SbomEntity();
                                    sbomEntity.fileId = sbomDto.fileId();
                                    sbomEntity.task = taskEntity;
                                    return sbomEntity;
                                })
                                .collect(Collectors.toList());
                    }

                    taskEntity.persist();
                    return taskMapper.toDto(taskEntity);
                })
                .map(dto -> RestResponse.ResponseBuilder
                        .<TaskDto>create(RestResponse.Status.OK)
                        .entity(dto)
                        .build()
                )
                .orElse(RestResponse.ResponseBuilder
                        .<TaskDto>create(RestResponse.Status.NOT_FOUND)
                        .build()
                );
    }

}
