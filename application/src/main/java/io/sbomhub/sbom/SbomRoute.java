package io.sbomhub.sbom;

import io.sbomhub.sbom.models.PackageJsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SbomRoute extends RouteBuilder {

    public static final String SBOM_ID = "SBOM_ID";
    public static final String SBOM_FILE_ID = "SBOM_FILE_ID";

    @ConfigProperty(name = "messaging.type")
    String schedulerType;

    @ConfigProperty(name = "storage.type")
    String storageType;

    @ConfigProperty(name = "messaging.jsm.queue")
    String jmsQueue;

    @ConfigProperty(name = "messaging.sqs.queue")
    String sqsQueue;

    @Override
    public void configure() throws Exception {
        from("direct:schedule-sbom-analysis")
                .choice()
                .when(simple("{{messaging.type}}").isEqualToIgnoreCase("jvm"))
                .to("seda:schedule-sbom-analysis?waitForTaskToComplete=Never")
                .endChoice()
                .when(simple("{{messaging.type}}").isEqualToIgnoreCase("jms"))
                .to(ExchangePattern.InOnly, "jms:queue:" + jmsQueue + "?connectionFactory=#connectionFactory")
                .endChoice()
                .when(simple("{{messaging.type}}").isEqualToIgnoreCase("sqs"))
                .toD("aws2-sqs://" + sqsQueue + "?amazonSQSClient=#amazonSQSClient&autoCreateQueue=true")
                .endChoice()
                .end();

        from("direct:analyse-sbom")
                .setHeader(SBOM_ID, body())
                .bean("sbomBean", "fetchSbomAndUpdateStatusToStartProcessing")

                .setBody(header(SBOM_FILE_ID))
                .to("direct:" + storageType + "-get-file")

                .split()
                    .jsonpathWriteAsString("$.packages[*]")
                    .streaming()
                    .unmarshal().json(JsonLibrary.Jsonb, PackageJsonNode.class)
                    .choice()
                        .when(exchange -> exchange.getProperty(Exchange.SPLIT_SIZE) != null)
                            .bean("sbomBean", "updateSbomAndSetExpectedPackagesCount")
                        .endChoice()
                    .end()
                    .aggregate(header(SBOM_ID))
                        .aggregationStrategy(AggregationStrategies.groupedBody())
                        .completionSize(50)
                        .completionTimeout(1000)
                        .completionOnNewCorrelationGroup()
                        .completeAllOnStop()
                        .bean("sbomBean", "savePackages")
                    .end()
                .end();
    }

}
