
package io.sbomhub.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class JvmMessagesRoute extends RouteBuilder {

    @ConfigProperty(name = "messaging.type")
    String schedulerType;

    @Override
    public void configure() throws Exception {
        from("seda:schedule-sbom-analysis")
                .precondition(String.valueOf(schedulerType.equalsIgnoreCase("jvm")))
                .to("direct:analyse-sbom");
    }

}
