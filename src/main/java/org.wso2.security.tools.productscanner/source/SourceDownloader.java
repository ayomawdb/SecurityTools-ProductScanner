package org.wso2.security.tools.productscanner.source;

import org.wso2.security.tools.productscanner.pojo.RepoArtifact;

import java.io.File;

/**
 * Created by ayoma on 4/18/17.
 */
public interface SourceDownloader {
    public void downloadSource(String consoleTag, RepoArtifact repoArtifact, String fileName, File destinationFolder);
}
