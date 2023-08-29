package io.sbomhub.tasks;

import io.sbomhub.dto.TaskDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.camel.ProducerTemplate;

@ApplicationScoped
public class TaskWatcher {

    @Inject
    ProducerTemplate producerTemplate;

    public void onEvent(@Observes TaskDto taskDto) {
        producerTemplate.requestBodyAndHeaders("direct:create-job", taskDto, null);
    }

}
