package org.wso2.security.tools.productscanner.scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.wso2.security.tools.productscanner.AppConfig;
import org.wso2.security.tools.productscanner.pojo.Product;
import org.wso2.security.tools.productscanner.pojo.ProductArtifact;
import org.wso2.security.tools.productscanner.pojo.ProductDeploymentArtifact;
import org.wso2.security.tools.productscanner.source.SourceDownloader;
import org.wso2.security.tools.productscanner.storage.Storage;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by ayoma on 4/17/17.
 */
public class Carbon4ProductZipScanner implements ProductScanner{
    private static Logger log = Logger.getLogger(Carbon4ProductZipScanner.class.getName());

    private File scanFile;
    private SourceDownloader sourceDownloader;

    public Carbon4ProductZipScanner(File scanFile, SourceDownloader sourceDownloader) {
        this.scanFile = scanFile;
        this.sourceDownloader = sourceDownloader;
    }

    @Override
    public void scan(Storage storage) throws Exception {
        //String fileName = scanFile.getName();
        String consoleTag =  "["+scanFile.getName()+"] ";
        String fileId = scanFile.getName().toLowerCase().replace(".zip", "");
        String[] fileNameSplits = fileId.split("-");

        //Get product name and version
        String productName = null;
        String productVersion = null;
        if(fileNameSplits.length == 1) {
            productName = fileNameSplits[0];
            productVersion = "[Unknown]";
            log.warn(consoleTag + "[Unexpected] Product version unknown for " + scanFile.getAbsolutePath());
        } else if(fileNameSplits.length == 2) {
            productName = fileNameSplits[0];
            productVersion = fileNameSplits[1];
        } else if(fileNameSplits.length > 2) {
            productName = fileId.substring(0, fileId.indexOf('-'));
            productVersion = fileId.substring(fileId.indexOf('-'), fileId.length());
            log.warn(consoleTag + "[Unexpected] Product version out of pattern for " + scanFile.getAbsolutePath());
        }

        //Extract ZIP to a folder
        File extractLocation = new File(scanFile.getParentFile().getPath() + File.separator + fileId);
        if(extractLocation.exists()) {
            log.warn(consoleTag + "Cleaning extract location: "+extractLocation.getAbsolutePath());
            FileUtils.deleteDirectory(extractLocation);
        }
        extractLocation.mkdir();
        ZipUtil.unpack(scanFile, extractLocation);

        //Locate version text file
        String versionText = null;
        Collection<File> sourceFiles = FileUtils.listFilesAndDirs(extractLocation, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : sourceFiles) {
            if(file.getAbsolutePath().toLowerCase().endsWith(File.separator + "bin" + File.separator + "version.txt")) {
                versionText  = FileUtils.readFileToString(file, StandardCharsets.US_ASCII).replace("\r","").replace("\n","");
                break;
            }
        }
        log.info(consoleTag + "Version text read: " + versionText);

        //Locate carbon version text file
        String carbonVersionText = null;
        for (File file : sourceFiles) {
            if(file.getAbsolutePath().toLowerCase().endsWith(File.separator + "bin" + File.separator + "wso2carbon-version.txt")) {
                carbonVersionText  = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
                break;
            }
        }
        log.info(consoleTag + "Carbon version text read: " + versionText);

        //Find JAR or WAR files for cross checking with repo information
        List<File> inScopeFileCollection = new ArrayList<File>();
        for (File file : sourceFiles) {
            if(file.getAbsolutePath().toLowerCase().endsWith(".jar") || file.getAbsolutePath().toLowerCase().endsWith(".war") || file.getAbsolutePath().toLowerCase().endsWith(".mar")) {
                inScopeFileCollection.add(file);
            }
        }

        List<Future<Carbon4ProductZipScannerDbLookupTask.RepoArtifactInternal>> futureList = new ArrayList<Future<Carbon4ProductZipScannerDbLookupTask.RepoArtifactInternal>>();
        ExecutorService executorService = Executors.newFixedThreadPool(AppConfig.getDbLookupWorkerThreadCount());
        for(File file : inScopeFileCollection) {
            log.debug(consoleTag + "Adding artifact into database scan: " + file.getAbsolutePath());
            Callable<Carbon4ProductZipScannerDbLookupTask.RepoArtifactInternal> callable = new Carbon4ProductZipScannerDbLookupTask(consoleTag, storage, file, sourceDownloader);
            Future<Carbon4ProductZipScannerDbLookupTask.RepoArtifactInternal> futureRepoArtifactInfo = executorService.submit(callable);
            futureList.add(futureRepoArtifactInfo);
        }
        executorService.shutdown();

        //Wait for completion of all the threads
        log.info(consoleTag + "Started waiting for DbLookup threads to complete.");
        String finalProductName = productName;
        String finalProductVersion = productVersion;
        String finalCarbonVersionText = carbonVersionText;
        String finalVersionText = versionText;
        futureList.parallelStream().forEach(artifactInfoFuture -> {
            Carbon4ProductZipScannerDbLookupTask.RepoArtifactInternal repoArtifact = null;
            try {
                repoArtifact = artifactInfoFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (repoArtifact != null) {
                String formattedFilePath = repoArtifact.getFile().getAbsolutePath().substring(extractLocation.getAbsolutePath().length(), repoArtifact.getFile().getAbsolutePath().length());

                Product product = new Product(scanFile.getName(), finalProductName, finalProductVersion, finalCarbonVersionText, finalVersionText);
                ProductArtifact productArtifact = new ProductArtifact(product, repoArtifact.getFile().getName(), formattedFilePath, repoArtifact.getRepoArtifact());

                List<File> deploymentArtifactCollection = new ArrayList<File>();
                for (File file : sourceFiles) {
                    if (file.getAbsolutePath().toLowerCase().contains("repository" + File.separator + "deployment" + File.separator + "server" + File.separator) && file.isDirectory()) {
                        deploymentArtifactCollection.add(file);
                    }
                }

                storage.persist(productArtifact);

                for (File deploymentArtifactFile : deploymentArtifactCollection) {
                    String formattedDeploymentArtifactFile = deploymentArtifactFile.getAbsolutePath().substring(extractLocation.getAbsolutePath().length(), deploymentArtifactFile.getAbsolutePath().length());
                    ProductDeploymentArtifact productDeploymentArtifact = new ProductDeploymentArtifact(product, formattedDeploymentArtifactFile);

                    storage.persist(productDeploymentArtifact);
                }

                if (repoArtifact.getRepoArtifact() == null) {
                    log.error(consoleTag + "[DBMiss] No database entry for artifact: " + repoArtifact.getFile().getAbsolutePath());
                } else {
                    log.info(consoleTag + "[DbHit] Database entry found for artifact: " + repoArtifact.getFile().getAbsolutePath());
                    if (AppConfig.isDownloadSource()) {
                        log.info(consoleTag + "Source download started for: " + repoArtifact.getFile().getAbsolutePath());
                        String formattedFileName = repoArtifact.getFile().getName().substring(0, repoArtifact.getFile().getName().lastIndexOf('.'));
                        sourceDownloader.downloadSource(consoleTag, repoArtifact.getRepoArtifact(), formattedFileName, scanFile.getParentFile());
                    }
                }
            }
        });
        //for (Future<Carbon4ProductZipScannerDbLookupTask.RepoArtifactInternal> artifactInfoFuture : futureList) {
        //}

        //Do cleanup and storage release
        log.info(consoleTag + "All DbLookup threads completed. Clean up tasks started.");
        FileUtils.deleteDirectory(extractLocation);
    }
}
