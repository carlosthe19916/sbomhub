package io.sbomhub.resources.models;

import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class SbomFileForm {
    @RestForm("tag")
    @PartType(MediaType.TEXT_PLAIN)
    public String tag;

    @RestForm("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload file;
}
