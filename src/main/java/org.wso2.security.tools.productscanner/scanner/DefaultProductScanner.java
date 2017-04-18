package org.wso2.security.tools.productscanner.scanner;

import org.apache.log4j.Logger;
import org.wso2.security.tools.productscanner.AppConfig;
import org.wso2.security.tools.productscanner.source.DefaultSourceDownloader;
import org.wso2.security.tools.productscanner.source.SourceDownloader;
import org.wso2.security.tools.productscanner.storage.Storage;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by ayoma on 4/17/17.
 */
public class DefaultProductScanner implements ProductScanner {
    private static Logger log = Logger.getLogger(DefaultProductScanner.class.getName());

    @Override
    public void scan(Storage storage) throws Exception {
        SourceDownloader sourceDownloader = new DefaultSourceDownloader();
        if(AppConfig.getScanPath().getAbsolutePath().toLowerCase().endsWith(".zip")) {
            log.info("Submitting single product ZIP file for scan: " + AppConfig.getScanPath().getAbsolutePath());
            ProductScanner defaultProductZipScanner = new Carbon4ProductZipScanner(AppConfig.getScanPath(), sourceDownloader);
            defaultProductZipScanner.scan(storage);
        } else {
            if(AppConfig.getScanPath().isDirectory()) {

                ExecutorService executorService = Executors.newFixedThreadPool(AppConfig.getProductScanWorkerThreadCount());
                for(File childFile : AppConfig.getScanPath().listFiles()) {
                    log.info("Submitting product ZIP file for scan: " + childFile.getAbsolutePath());
                    Runnable runnable = new DefaultProductScannerProductScanTask(storage, childFile, sourceDownloader);
                    executorService.submit(runnable);
                }
                executorService.shutdown();

                //Wait for completion of all the threads
                log.info("Started waiting for ProductScan threads to complete.");
                try {
                    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                } catch (InterruptedException e) {
                    throw e;
                }

                //Do cleanup and storage release
                log.info("All ProductScan threads completed.");
            } else {
                log.error("Scan path is invalid (" + AppConfig.getScanPath().getAbsolutePath() + "). Path should be a folder or a ZIP file. Terminating...");
                return;
            }
        }
    }
}
