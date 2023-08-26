/*
 * Copyright 2019 Project OpenUBL, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sbomhub.tasks;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kubernetes.KubernetesConstants;
import org.apache.camel.component.kubernetes.KubernetesOperations;

import java.util.*;

@ApplicationScoped
public class K8sRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:create-job")
            .process(exchange -> {
                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_JOB_NAME, "camel-job");
                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_NAMESPACE_NAME, "default");

                    Map<String, String> labels = new HashMap<>();
                    labels.put("jobLabelKey1", "value1");
                    labels.put("jobLabelKey2", "value2");
                    labels.put("app", "jobFromCamelApp");
                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_JOB_LABELS, labels);

                    exchange.getIn().setHeader(KubernetesConstants.KUBERNETES_JOB_SPEC, generateJobSpec());
                })
            .toF("kubernetes-job:///?operation=" + KubernetesOperations.CREATE_JOB_OPERATION)
            .process(exchange -> {
                    System.out.println(exchange);
                });
    }

    private JobSpec generateJobSpec() {
        JobSpec js = new JobSpec();

        PodTemplateSpec pts = new PodTemplateSpec();

        PodSpec ps = new PodSpec();
        ps.setRestartPolicy("Never");
        ps.setContainers(generateContainers());
        pts.setSpec(ps);

        ObjectMeta metadata = new ObjectMeta();
        Map<String, String> annotations = new HashMap<String, String>();
        annotations.put("jobMetadataAnnotation1", "random value");
        metadata.setAnnotations(annotations);

        Map<String, String> podlabels = new HashMap<String, String>();
        podlabels.put("podLabelKey1", "value1");
        podlabels.put("podLabelKey2", "value2");
        podlabels.put("app", "podFromCamelApp");
        metadata.setLabels(podlabels);

        pts.setMetadata(metadata);
        js.setTemplate(pts);
        return js;
    }

    private List<Container> generateContainers() {
        Container container = new Container();
        container.setName("pi");
        container.setImage("perl");
        List<String> command = new ArrayList<>();
        command.add("echo");
        command.add("Job created from Apache Camel code at " + (new Date()));
        container.setCommand(command);
        List<Container> containers = new ArrayList<Container>();
        containers.add(container);
        return containers;
    }
}
