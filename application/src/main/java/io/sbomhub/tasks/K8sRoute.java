package io.sbomhub.tasks;

import io.fabric8.kubernetes.api.model.*;
import io.sbomhub.dto.RepositoryType;
import io.sbomhub.dto.TaskDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kubernetes.KubernetesConstants;
import org.apache.camel.component.kubernetes.KubernetesOperations;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;

@ApplicationScoped
public class K8sRoute extends RouteBuilder {

    private static final String ACCESSKEYID_KEY = "access_key_id";
    private static final String SECRETACCESSKEY_KEY = "secret_access_key";

    @ConfigProperty(name = "sbomhub.namespace")
    String sbomhubNamespace;

    @ConfigProperty(name = "sbomhub.url")
    String sbomhubUrl;

    @ConfigProperty(name = "storage.type")
    String storageType;

    @ConfigProperty(name = "storage.host")
    Optional<String> storageHost;

    @ConfigProperty(name = "storage.bucket")
    String storageBucket;

    @ConfigProperty(name = "storage.region")
    Optional<String> storageRegion;

    @ConfigProperty(name = "storage.access_key_id")
    String storageAccessKeyId;

    @ConfigProperty(name = "storage.secret_access_key")
    String storageSecretAccessKey;

    @Override
    public void configure() throws Exception {
        from("direct:create-job")
            .process(exchange -> {
                    TaskDto taskDto = exchange.getIn().getBody(TaskDto.class);
                    exchange.getIn().setHeader("task", taskDto);
                })

                // Create secrets
            .process(exchange -> {
                    TaskDto taskDto = exchange.getIn().getHeader("task", TaskDto.class);
                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_NAMESPACE_NAME, sbomhubNamespace);
                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_SECRET, generateSecret(taskDto));
                })
            .toF("kubernetes-secrets:///?operation=" + KubernetesOperations.CREATE_SECRET_OPERATION)

                // Create pod
            .process(exchange -> {
                    TaskDto taskDto = exchange.getIn().getHeader("task", TaskDto.class);
                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_NAMESPACE_NAME, sbomhubNamespace);
                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_POD_SPEC, generatePodSpec(taskDto));
                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_POD_NAME, taskDto.name());
                })
            .toF("kubernetes-pods:///?operation=" + KubernetesOperations.CREATE_POD_OPERATION);
    }

    private Map<String, String> generateLabels(TaskDto taskDto) {
        return Map.of(
                "app", "sbomhub",
                "role", "task",
                "task", taskDto.id().toString()
        );
    }

    private Secret generateSecret(TaskDto taskDto) {
        return new SecretBuilder()
                .withNewMetadata()
                .withName(taskDto.name())
                .withLabels(generateLabels(taskDto))
                .endMetadata()
                .addToStringData(ACCESSKEYID_KEY, storageAccessKeyId)
                .addToStringData(SECRETACCESSKEY_KEY, storageSecretAccessKey)
                .build();
    }

    private PodSpec generatePodSpec(TaskDto taskDto) {
        String taskImage = taskDto.repository().taskImage();
        List<EnvVar> envVars = new ArrayList<>();

        envVars.add(new EnvVarBuilder().withName("SBOMHUB_URL")
                .withValue(sbomhubUrl)
                .build()
        );
        envVars.add(new EnvVarBuilder().withName("SBOMHUB_TASK")
                .withValue(taskDto.id().toString())
                .build()
        );

        // Git or Registry
        if (taskDto.repository().type().equals(RepositoryType.Git)) {
            envVars.add(new EnvVarBuilder().withName("GIT_REPOSITORY")
                    .withValue(taskDto.repository().url())
                    .build()
            );
        } else {
            envVars.add(new EnvVarBuilder().withName("CONTAINER_REPOSITORY")
                    .withValue(taskDto.repository().url())
                    .build()
            );
        }

        if (taskDto.repository().gitDetails() != null) {
            envVars.add(new EnvVarBuilder().withName("GIT_REF")
                    .withValue(taskDto.repository().gitDetails().ref())
                    .build()
            );
            envVars.add(new EnvVarBuilder().withName("GIT_ROOT_PATH")
                    .withValue(taskDto.repository().gitDetails().rootPath())
                    .build()
            );
        }

        // Storage
        envVars.add(new EnvVarBuilder().withName("STORAGE_TYPE")
                .withValue(storageType)
                .build()
        );
        envVars.add(new EnvVarBuilder().withName("STORAGE_HOST")
                .withValue(storageHost.orElse(""))
                .build()
        );
        envVars.add(new EnvVarBuilder().withName("STORAGE_BUCKET")
                .withValue(storageBucket)
                .build()
        );
        envVars.add(new EnvVarBuilder().withName("STORAGE_REGION")
                .withValue(storageRegion.orElse("storageRegion"))
                .build()
        );
        envVars.add(new EnvVarBuilder().withName("STORAGE_ACCESS_KEY_ID")
                .withNewValueFrom()
                .withNewSecretKeyRef()
                .withName(taskDto.name())
                .withKey(ACCESSKEYID_KEY)
                .withOptional(false)
                .endSecretKeyRef()
                .endValueFrom()
                .build()
        );
        envVars.add(new EnvVarBuilder().withName("STORAGE_SECRET_ACCESS_KEY")
                .withNewValueFrom()
                .withNewSecretKeyRef()
                .withName(taskDto.name())
                .withKey(SECRETACCESSKEY_KEY)
                .withOptional(false)
                .endSecretKeyRef()
                .endValueFrom()
                .build()
        );

        return new PodSpecBuilder()
                .withRestartPolicy("Always")
                .withContainers(new ContainerBuilder()
                        .withName("task")
                        .withImage(taskImage)
                        .withImagePullPolicy("Always")
                        .withEnv(envVars)
                        .withResources(new ResourceRequirementsBuilder()
                                .withRequests(Map.of(
                                        "cpu", new Quantity("0.5"),
                                        "memory", new Quantity("0.5Gi")
                                ))
                                .withLimits(Map.of(
                                        "cpu", new Quantity("1"),
                                        "memory", new Quantity("1Gi")
                                ))
                                .build()
                        )
                        .withVolumeMounts(new VolumeMountBuilder()
                                .withName("workspace-pvol")
                                .withMountPath("/opt/sbomhub/workspace")
                                .build()
                        )
                        .build()
                )
                .withVolumes(new VolumeBuilder()
                        .withName("workspace-pvol")
                        .withNewEmptyDir()
                        .endEmptyDir()
                        .build()
                )
                .build();
    }

}
