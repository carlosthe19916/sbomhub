package io.sbomhub.storage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class S3Route extends RouteBuilder {

    @ConfigProperty(name = "storage.type")
    String storageType;

    @ConfigProperty(name = "storage.bucket")
    String s3Bucket;

    @ConfigProperty(name = "storage.access_key_id")
    String s3AccessKeyID;

    @ConfigProperty(name = "storage.secret_access_key")
    String s3SecretAccessKey;

    @ConfigProperty(name = "storage.region")
    String s3Region;

    @ConfigProperty(name = "storage.host")
    Optional<String> s3Host;

    @Produces
    @Named("s3Presigner")
    public S3Presigner produceS3Client() {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(s3AccessKeyID, s3SecretAccessKey);
        AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(awsCredentials);

        S3Presigner.Builder s3PreSignerBuilder = S3Presigner.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.of(s3Region));

        S3Presigner s3Presigner;
        if (s3Host.isPresent()) {
            s3Presigner = s3PreSignerBuilder
                    .endpointOverride(URI.create(s3Host.get()))
                    .build();
        } else {
            s3Presigner = s3PreSignerBuilder
                    .build();
        }
        return s3Presigner;
    }

    @Produces
    @Named("s3client")
    public S3Client produceS3Presigner() {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(s3AccessKeyID, s3SecretAccessKey);
        AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(awsCredentials);

        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.of(s3Region));

        S3Client s3Client;
        if (s3Host.isPresent()) {
            s3Client = s3ClientBuilder
                    .endpointOverride(URI.create(s3Host.get()))
                    .build();
        } else {
            s3Client = s3ClientBuilder
                    .build();
        }

        return s3Client;
    }

    @Override
    public void configure() throws Exception {
        from("direct:s3-save-file")
                .precondition(String.valueOf(storageType.equalsIgnoreCase("s3")))
                .process(exchange -> {
                    String filename = UUID.randomUUID().toString();

                    exchange.getIn().setHeader(AWS2S3Constants.KEY, filename);
                    exchange.getIn().setHeader(AWS2S3Constants.BUCKET_DESTINATION_NAME, s3Bucket);
                })
                .toD("aws2-s3://" + s3Bucket + "?autoCreateBucket=true&deleteAfterWrite=true&amazonS3Client=#s3client")
                .process(exchange -> {
                    String documentID = exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class);
                    exchange.getIn().setBody(documentID);
                });

        from("direct:s3-get-file")
                .precondition(String.valueOf(storageType.equalsIgnoreCase("s3")))
                .pollEnrich().simple("aws2-s3://" + s3Bucket + "?amazonS3Client=#s3client&deleteAfterRead=false&fileName=${body}")
                .setHeader("Content-Disposition", simple("$header.CamelAwsS3ContentDisposition"))
                .setHeader(Exchange.CONTENT_TYPE, simple("$header.CamelAwsS3ContentType"));
    }

}