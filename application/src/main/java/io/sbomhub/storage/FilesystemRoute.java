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
package io.sbomhub.storage;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

@ApplicationScoped
public class FilesystemRoute extends RouteBuilder {

    @ConfigProperty(name = "storage.type")
    String storageType;

    @ConfigProperty(name = "storage.filesystem.directory")
    String fileSystemFolder;

    @Override
    public void configure() throws Exception {
        from("direct:filesystem-save-file")
                .id("filesystem-save-file")
                .precondition(String.valueOf(storageType.equalsIgnoreCase("filesystem")))
                .process(exchange -> {
                    String filename = UUID.randomUUID().toString();

                    exchange.getIn().setHeader("folderName", fileSystemFolder);
                    exchange.getIn().setHeader(FileConstants.FILE_NAME, filename);
                })
                .toD("file:${header.folderName}")
                .process(exchange -> {
                    String folderName = exchange.getIn().getHeader("folderName", String.class);
                    String fileName = exchange.getIn().getHeader(FileConstants.FILE_NAME, String.class);

                    Path resolve = Paths.get(folderName).resolve(fileName);
                    exchange.getIn().setBody(resolve.toString());
                });

        from("direct:filesystem-get-file")
                .id("filesystem-get-file")
                .precondition(String.valueOf(storageType.equalsIgnoreCase("filesystem")))
                .process(exchange -> {
                    String filename = exchange.getIn().getBody(String.class);
                    byte[] bytes = Files.readAllBytes(Paths.get(filename));
                    exchange.getIn().setBody(bytes);
                });
    }

}
