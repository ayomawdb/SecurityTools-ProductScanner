package org.wso2.security.tools.productscanner.scanner;

import org.apache.log4j.Logger;
import org.wso2.security.tools.productscanner.source.SourceDownloader;
import org.wso2.security.tools.productscanner.storage.Storage;

import java.io.File;

/**
 * Created by ayoma on 4/17/17.
 */
public class DefaultProductScannerProductScanTask implements Runnable {
    private static Logger log = Logger.getLogger(DefaultProductScannerProductScanTask.class.getName());

    private Storage storage;
    private File childFile;
    private SourceDownloader sourceDownloader;

    public DefaultProductScannerProductScanTask(Storage storage, File childFile, SourceDownloader sourceDownloader) {
        this.storage = storage;
        this.childFile = childFile;
        this.sourceDownloader = sourceDownloader;
    }

    @Override
    public void run() {
        try {
            if (childFile.getAbsolutePath().toLowerCase().endsWith(".zip")) {
                //TODO: Add support for Carbon 5 based products
                ProductScanner defaultProductZipScanner = new Carbon4ProductZipScanner(childFile, sourceDownloader);
                defaultProductZipScanner.scan(storage);
            } else {
                log.warn("[Skipping] Not a valid file for scanning: " + childFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.fatal("Exception occured in processing file: " + childFile.getAbsolutePath(), e);
        }
    }
}
