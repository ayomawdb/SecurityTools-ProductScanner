package org.wso2.security.tools.productscanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayoma on 4/14/17.
 */
public class AppConfig {
    private static boolean verbose;
    private static boolean debug;
    private static boolean createDB;

    private static boolean downloadSource;

    private static int dbLookupWorkerThreadCount = 1;
    private static int productScanWorkerThreadCount = 1;

    private static File scanPath;

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        AppConfig.verbose = verbose;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        AppConfig.debug = debug;
    }

    public static boolean isCreateDB() {
        return createDB;
    }

    public static void setCreateDB(boolean createDB) {
        AppConfig.createDB = createDB;
    }

    public static File getScanPath() {
        return scanPath;
    }

    public static void setScanPath(File scanPath) {
        AppConfig.scanPath = scanPath;
    }

    public static int getDbLookupWorkerThreadCount() {
        return dbLookupWorkerThreadCount;
    }

    public static void setDbLookupWorkerThreadCount(int dbLookupWorkerThreadCount) {
        AppConfig.dbLookupWorkerThreadCount = dbLookupWorkerThreadCount;
    }

    public static int getProductScanWorkerThreadCount() {
        return productScanWorkerThreadCount;
    }

    public static void setProductScanWorkerThreadCount(int productScanWorkerThreadCount) {
        AppConfig.productScanWorkerThreadCount = productScanWorkerThreadCount;
    }

    public static boolean isDownloadSource() {
        return downloadSource;
    }

    public static void setDownloadSource(boolean downloadSource) {
        AppConfig.downloadSource = downloadSource;
    }
}
