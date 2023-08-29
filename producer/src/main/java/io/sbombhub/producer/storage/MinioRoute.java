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
package io.sbombhub.producer.storage;

import io.minio.MinioClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.minio.MinioConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MinioRoute extends RouteBuilder {

    @ConfigProperty(name = "storage.type")
    String storageType;

    @ConfigProperty(name = "storage.bucket")
    String s3Bucket;

    @ConfigProperty(name = "storage.access_key_id")
    String s3AccessKeyID;

    @ConfigProperty(name = "storage.secret_access_key")
    String s3SecretAccessKey;

    @ConfigProperty(name = "storage.host")
    Optional<String> s3Host;

    @Produces
    @Singleton
    @Named("minioClient")
    public MinioClient produceS3client() {
        return MinioClient.builder()
                .endpoint(s3Host.orElse(""))
                .credentials(s3AccessKeyID, s3SecretAccessKey)
                .build();
    }

    @Override
    public void configure() throws Exception {
        from("direct:minio-save-file")
                .id("minio-save-file")
                .precondition(String.valueOf(storageType.equalsIgnoreCase("minio")))
                .choice()
                    .when(header("shouldZipFile").isEqualTo(true))
                        .marshal().zipFile()
                    .endChoice()
                .end()
                .process(exchange -> {
                    String filename = UUID.randomUUID().toString();

                    exchange.getIn().setHeader(MinioConstants.OBJECT_NAME, filename);
                    exchange.getIn().setHeader(MinioConstants.DESTINATION_BUCKET_NAME, s3Bucket);
                })
                .toD("minio://" + s3Bucket + "?autoCreateBucket=true&deleteAfterWrite=true&minioClient=#minioClient")
                .process(exchange -> {
                    String documentID = exchange.getIn().getHeader(MinioConstants.OBJECT_NAME, String.class);
                    exchange.getIn().setBody(documentID);
                });
    }

}
