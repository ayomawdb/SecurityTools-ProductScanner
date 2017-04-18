package org.wso2.security.tools.productscanner.downloader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.wso2.security.tools.productscanner.pojo.Repo;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by ayoma on 4/14/17.
 */
public class GitRepoDownloader implements RepoDownloader {
    private static Logger log = Logger.getLogger(GitRepoDownloader.class.getName());

    public void downloadRepo(String consoleTag, Repo repo, File destinationFolder) throws IOException {
        synchronized (GitRepoDownloader.class) {
            File sourceCacheFolder = new File("source-cache");
            if (!sourceCacheFolder.exists()) {
                sourceCacheFolder.mkdir();
            }

            File tempZipFile = new File(destinationFolder.getAbsoluteFile() + File.separator + repo.getRepositoryName() + "-Tag-" + repo.getTagName() + ".zip");

            File cachedSource = locate(sourceCacheFolder, tempZipFile.getName());
            if (cachedSource == null) {
                log.info(consoleTag + "[CacheMiss] Unable to locate " + repo.getRepositoryName() + "-Tag-" + repo.getTagName() + " in \"source-cache\"");
                log.info(consoleTag + "Downloading started");
                downloadFile(repo.getTagZip(), tempZipFile);
                log.info(consoleTag + "Downloading completed");

                FileUtils.copyFile(tempZipFile, sourceCacheFolder);
                unzip(tempZipFile, destinationFolder);

                log.info(consoleTag + "Extracted and cached: " + tempZipFile.getAbsolutePath());
            } else {
                log.info(consoleTag + "[CacheHit] Located " + repo.getRepositoryName() + "-Tag-" + repo.getTagName() + " in \"source-cache\"");
                unzip(cachedSource, destinationFolder);
                log.info(consoleTag + "Extracted: " + cachedSource.getAbsolutePath());
            }
        }
    }

    private void downloadFile(String sourceUrl, File destinationFile) throws IOException {
        URL website = new URL(sourceUrl);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destinationFile);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    private void unzip(File zipFile, File zipExtractFolder) {
        ZipUtil.unpack(zipFile, zipExtractFolder);
    }

    private File locate(File sourceFolder, String fileName) {
        File[] sourceFiles = sourceFolder.listFiles();
        for (File file : sourceFiles) {
            if(file.getAbsolutePath().toLowerCase().endsWith(File.pathSeparator + fileName)) {
                return file;
            }
        }
        return null;
    }
}
