package org.wso2.security.tools.productscanner.source;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.wso2.security.tools.productscanner.downloader.GitRepoDownloader;
import org.wso2.security.tools.productscanner.downloader.RepoDownloader;
import org.wso2.security.tools.productscanner.pojo.RepoArtifact;
import org.wso2.security.tools.productscanner.pojo.RepoType;

import java.io.File;

/**
 * Created by ayoma on 4/18/17.
 */
public class DefaultSourceDownloader implements SourceDownloader {
    private static Logger log = Logger.getLogger(DefaultSourceDownloader.class.getName());

    @Override
    public void downloadSource(String consoleTag, RepoArtifact repoArtifact, String fileName, File destinationFolder) {
        File sourceTempFolder = new File("source-temp");
        synchronized (DefaultSourceDownloader.class) {
            if (!sourceTempFolder.exists()) {
                sourceTempFolder.mkdir();
            }
        }

        sourceTempFolder = new File("source-temp" + File.separator + fileName);
        sourceTempFolder.mkdir();
        log.info(consoleTag + "Created temp folder for source processing at: " + sourceTempFolder.getAbsolutePath());

        File sourceFolder = new File(destinationFolder.getAbsolutePath() + File.separator + fileName);
        sourceFolder.mkdir();
        log.info(consoleTag + "Created source destination folder at: " + sourceFolder.getAbsolutePath());

        if(repoArtifact.getRepo().getRepoType().equals(RepoType.GIT)) {
            RepoDownloader repoDownloader = new GitRepoDownloader();
            try {
                repoDownloader.downloadRepo(consoleTag, repoArtifact.getRepo(), sourceTempFolder);
                File[] extractedFolderContent = sourceTempFolder.listFiles();
                if(extractedFolderContent.length == 0) {
                    log.error(consoleTag + "[Unexpected] Upexpected number of files ("+extractedFolderContent.length+") in: " + sourceTempFolder.getAbsolutePath());
                } else if(extractedFolderContent.length > 0) {
                    if(extractedFolderContent.length > 1) {
                        log.error(consoleTag + "[Unexpected] Upexpected number of files ("+extractedFolderContent.length+") in: " + sourceTempFolder.getAbsolutePath());
                    }
                    File actualRepoSourceFolder = extractedFolderContent[0];
                    log.info(consoleTag + "ActualRepoSourceFolder identified as: " + actualRepoSourceFolder.getAbsolutePath());

                    File actualSourceFolder = new File(actualRepoSourceFolder + File.separator + repoArtifact.getPath());
                    log.info(consoleTag + "ActualSourceFolder identified as: " + actualSourceFolder.getAbsolutePath());

                    log.info(consoleTag + "Copying ActualSourceFolder to: " + sourceFolder.getAbsolutePath());
                    FileUtils.copyDirectory(actualSourceFolder, sourceFolder);
                }
                log.info(consoleTag + "Deleting temp folder for source processing at: " + sourceTempFolder.getAbsolutePath());
                FileUtils.deleteDirectory(sourceTempFolder);
            } catch (Exception e) {
                log.error(consoleTag + "Unable to download repository " + repoArtifact.getRepo(), e);
            }
        } else {
            log.error(consoleTag + "[Unexpected] Upexpected repository type ("+repoArtifact.getRepo().getRepoType().toString()+") for: " + fileName);
        }
    }
}
