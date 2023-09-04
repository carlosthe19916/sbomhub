
package io.sbomhub.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import java.net.URI;
import java.util.Optional;

@ApplicationScoped
public class SqsMessagesRoute extends RouteBuilder {

    @ConfigProperty(name = "messaging.type")
    String schedulerType;

    @ConfigProperty(name = "messaging.sqs.queue")
    String sqsTopic;

    @ConfigProperty(name = "messaging.sqs.access_key_id")
    String sqsAccessKeyID;

    @ConfigProperty(name = "messaging.sqs.secret_access_key")
    String sqsSecretAccessKey;

    @ConfigProperty(name = "messaging.sqs.region")
    String sqsRegion;

    @ConfigProperty(name = "messaging.sqs.host")
    Optional<String> sqsHost;

    @Singleton
    @Produces
    @Named("amazonSQSClient")
    public SqsClient produceSqsClient() {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(sqsAccessKeyID, sqsSecretAccessKey);
        AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(awsCredentials);

        SqsClientBuilder clientBuilder = SqsClient.builder()
                .region(Region.of(sqsRegion))
                .credentialsProvider(awsCredentialsProvider);

        SqsClient client;
        if (sqsHost.isPresent()) {
            client = clientBuilder
                    .endpointOverride(URI.create(sqsHost.get()))
                    .build();
        } else {
            client = clientBuilder
                    .build();
        }

        return client;
    }

    @Override
    public void configure() throws Exception {
        from("aws2-sqs://" + sqsTopic + "?amazonSQSClient=#amazonSQSClient&autoCreateQueue=true")
                .precondition(String.valueOf(schedulerType.equalsIgnoreCase("sqs")))
                .to("direct:analyse-sbom");
    }

}
