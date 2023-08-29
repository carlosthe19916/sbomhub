package io.sbombhub.producer;

import io.sbomhub.dto.SbomDto;
import io.sbomhub.dto.TaskDto;
import io.sbomhub.dto.TaskState;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MainRoute extends RouteBuilder {

    @ConfigProperty(name = "git.repository")
    String gitRepository;

    @ConfigProperty(name = "git.ref")
    Optional<String> gitRef;

    @ConfigProperty(name = "git.root.path")
    Optional<String> gitRootPath;

    @ConfigProperty(name = "storage.type")
    String storageType;

    @ConfigProperty(name = "sbomhub.url")
    String sbomhubUrl;

    @ConfigProperty(name = "sbomhub.task")
    Long sbomhubTask;

    @Override
    public void configure() throws Exception {
        from("direct:analyze")
                .to("git://repository?remotePath=" + gitRepository + "&operation=clone")
                .to("exec:mvn?workingDir=repository&args=org.cyclonedx:cyclonedx-maven-plugin:2.7.9:makeAggregateBom -DoutputFormat=json -DschemaVersion=1.3")
                .setBody(constant(Paths.get("repository", "target", "bom.json").toFile()))
                .to("direct:" + storageType + "-save-file")
                .process(exchange -> {
                    String sbomFileId = exchange.getIn().getBody(String.class);

                    SbomDto sbomDto = new SbomDto(null, sbomFileId);
                    TaskDto taskDto = new TaskDto(sbomhubTask, null, TaskState.Succeeded, null, List.of(sbomDto));

                    exchange.getIn().setBody(taskDto);
                })
                .marshal()
                .json(JsonLibrary.Jackson)
                .to("http://" + sbomhubUrl + "/tasks/" + sbomhubTask + "?httpMethod=PUT")
                .process(exchange -> {
                    File repositoryFile = Paths.get("repository").toFile();
                    if (repositoryFile.exists() && repositoryFile.isDirectory()) {
                        FileUtils.deleteDirectory(repositoryFile);
                    }
                });
    }
}
