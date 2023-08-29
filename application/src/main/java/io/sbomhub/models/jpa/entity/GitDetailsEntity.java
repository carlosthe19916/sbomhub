package io.sbomhub.models.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GitDetailsEntity {

    // The branch, tag or SHA to checkout
    @Column(name = "ref")
    public String ref;

    @Column(name = "root_path")
    public String rootPath;

}
