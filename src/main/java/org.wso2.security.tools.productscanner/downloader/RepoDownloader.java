package org.wso2.security.tools.productscanner.downloader;

import org.wso2.security.tools.productscanner.pojo.Repo;

import java.io.File;

/**
 * Created by ayoma on 4/14/17.
 */
public interface RepoDownloader {
    public void downloadRepo(String consoleTag, Repo repo, File destinationFolder) throws Exception;
}
