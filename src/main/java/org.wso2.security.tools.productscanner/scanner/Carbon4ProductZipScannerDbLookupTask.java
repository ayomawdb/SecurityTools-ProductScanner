package org.wso2.security.tools.productscanner.scanner;

import org.apache.log4j.Logger;
import org.wso2.security.tools.productscanner.pojo.RepoArtifact;
import org.wso2.security.tools.productscanner.source.SourceDownloader;
import org.wso2.security.tools.productscanner.storage.Storage;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created by ayoma on 4/17/17.
 */
public class Carbon4ProductZipScannerDbLookupTask implements Callable<Carbon4ProductZipScannerDbLookupTask.RepoArtifactInternal> {
    private static Logger log = Logger.getLogger(Carbon4ProductZipScanner.class.getName());

    private Storage storage;
    private File file;
    private SourceDownloader sourceDownloader;
    private String consoleTag;

    public Carbon4ProductZipScannerDbLookupTask(String consoleTag, Storage storage, File file, SourceDownloader sourceDownloader) {
        this.storage = storage;
        this.file = file;
        this.sourceDownloader = sourceDownloader;
        this.consoleTag = consoleTag;
    }

    @Override
    public RepoArtifactInternal call() throws Exception {
        log.info(consoleTag + "Scanning:  " + file.getAbsolutePath());
        String finalName1 = file.getName().substring(0, file.getName().lastIndexOf('.'));
        log.info(consoleTag + "Scanning database for:  " + finalName1);

        RepoArtifact repoArtifact = storage.searchFinalName(finalName1);
        if(repoArtifact == null) {
            if(finalName1.contains("_")) {
                String finalName2 = finalName1.substring(0, finalName1.lastIndexOf('_')) + "-" + finalName1.substring(finalName1.lastIndexOf('_'), finalName1.length());
                log.info(consoleTag + "Scanning database for:  " + finalName2);
                repoArtifact = storage.searchFinalName(finalName2);
            }
        }
        RepoArtifactInternal repoArtifactInternal = new RepoArtifactInternal(repoArtifact,  file);
        return repoArtifactInternal;
    }

    public class RepoArtifactInternal {
        private RepoArtifact repoArtifact;
        private File file;

        public RepoArtifactInternal(RepoArtifact repoArtifact, File file) {
            this.repoArtifact = repoArtifact;
            this.file = file;
        }

        public RepoArtifact getRepoArtifact() {
            return repoArtifact;
        }

        public void setRepoArtifact(RepoArtifact repoArtifact) {
            this.repoArtifact = repoArtifact;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }
}
