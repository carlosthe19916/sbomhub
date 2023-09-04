package io.sbomhub.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.apache.camel.BindToRegistry;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class JmsMessagesRoute extends RouteBuilder {

    @ConfigProperty(name = "messaging.type")
    String schedulerType;

    @ConfigProperty(name = "messaging.jsm.queue")
    String jmsQueue;

    @Inject
    Instance<ConnectionFactory> connectionFactory;

    @BindToRegistry("connectionFactory")
    public ConnectionFactory connectionFactory() {
        if (connectionFactory.isResolvable()) {
            return connectionFactory.get();
        } else {
            return new ActiveMQJMSConnectionFactory();
        }
    }

    @Override
    public void configure() throws Exception {
        from("jms:queue:" + jmsQueue + "?connectionFactory=#connectionFactory")
                .precondition(String.valueOf(schedulerType.equalsIgnoreCase("jms")))
                .to("direct:analyse-sbom");
    }

}
