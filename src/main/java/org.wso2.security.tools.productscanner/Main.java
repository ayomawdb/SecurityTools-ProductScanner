package org.wso2.security.tools.productscanner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.wso2.security.tools.productscanner.scanner.DefaultProductScanner;
import org.wso2.security.tools.productscanner.scanner.ProductScanner;
import org.wso2.security.tools.productscanner.storage.JDBCStorage;
import org.wso2.security.tools.productscanner.storage.Storage;

import java.io.File;
import java.io.IOException;

/**
 * Created by ayoma on 4/14/17.
 */
public class Main {
    private static Logger log = Logger.getLogger(Main.class.getName());
    
    @Parameter(names = {"-path"}, description = "Product ZIP file or location to find multiple product ZIP files",order = 1)
    private String scanPath;

    @Parameter(names = {"-storage"}, description = "Storage used in storing final results (Options: JDBC) (Default: JDBC)", order = 3)
    private String storageType;

    @Parameter(names = {"-jdbc.driver"}, description = "Database driver class (Default: com.mysql.jdbc.Driver)", order = 4)
    private String databaseDriver;

    @Parameter(names = {"-jdbc.url"}, description = "Database connection URL (Default: jdbc:mysql://localhost/RepoScanner)", order = 5)
    private String databaseUrl;

    @Parameter(names = {"-jdbc.username"}, description = "Database username (Default: root)", order = 6)
    private String databaseUsername;

    @Parameter(names = {"-jdbc.password"}, description = "Database password", password = true, order = 7)
    private String databasePassword;

    @Parameter(names = {"-jdbc.dialect"}, description = "Database Hibernate dialect (Default: org.hibernate.dialect.MySQLDialect)", order = 8)
    private String databaseHibernateDialect;

    @Parameter(names = {"-verbose", "-v"}, description = "Verbose output", order = 9)
    private boolean verbose;

    @Parameter(names = {"-debug", "-d"}, description = "Verbose + Debug output for debugging requirements", order = 10)
    private boolean debug;

    @Parameter(names = {"--help", "-help", "-?"}, help = true, order = 11)
    private boolean help;

    @Parameter(names = {"-jdbc.create"}, description = "Drop and create JDBC tables", order = 12)
    private boolean databaseCreate;

    @Parameter(names = {"-source"}, description = "Download source code relevant to each product into a separate folder", order = 13)
    private boolean downloadSource;

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        log.info("-------------------------------------------------");
        log.info("-----                                       -----");
        log.info("-----            Product Scanner            -----");
        log.info("-----                                       -----");
        log.info("-------------------------------------------------");
        JCommander jCommander = new JCommander(main, args);
        jCommander.setProgramName("WSO2 Product Scanner");

        if(main.help) {
            jCommander.usage();
            return;
        }

        if(main.databaseDriver == null || main.databaseDriver.length() == 0) {
            main.databaseDriver = "com.mysql.jdbc.Driver";
        }
        if(main.databaseUrl == null || main.databaseUrl.length() == 0) {
            main.databaseUrl = "jdbc:mysql://localhost/RepoScanner";
        }
        if(main.databaseUsername == null || main.databaseUsername.length() == 0) {
            main.databaseUsername = "root";
        }
        if(main.databaseHibernateDialect == null || main.databaseHibernateDialect.length() == 0) {
            main.databaseHibernateDialect = "org.hibernate.dialect.MySQLDialect";
        }
        if(main.storageType == null || main.storageType.length() == 0) {
            main.storageType = "JDBC";
        }

        if(main.scanPath == null || main.scanPath.length() == 0) {
            log.error("Scan path not defined. Please use \"-path\" parameter to define scan path. Terminating...");
            jCommander.usage();
            return;
        } else {
            File scanPathFile = new File(main.scanPath);
            if (!scanPathFile.exists()) {
                log.error("Scan path is invalid (" + scanPathFile.getAbsolutePath() + "). Terminating...");
                return;
            } else if (!(scanPathFile.isDirectory() || scanPathFile.getAbsolutePath().toLowerCase().endsWith(".zip"))) {
                log.error("Scan path is invalid (" + scanPathFile.getAbsolutePath() + "). Path should be a folder or a ZIP file. Terminating...");
                return;
            }
            AppConfig.setScanPath(scanPathFile);
        }
        main.start(jCommander);
    }

    public void start(JCommander jCommander) {
        AppConfig.setVerbose(verbose);
        if(debug) {
            AppConfig.setVerbose(true);
            AppConfig.setDebug(true);
        }
        AppConfig.setCreateDB(databaseCreate);
        AppConfig.setDownloadSource(downloadSource);
        if(downloadSource) {
            File sourceTempFolder = new File("source-temp");
            File sourceCacheFolder = new File("source-cache");

            if(sourceTempFolder.exists()) {
                try {
                    FileUtils.deleteDirectory(sourceTempFolder);
                } catch (IOException e) {
                    log.error("Error in removing source-temp", e);
                }
            }
            if(sourceCacheFolder.exists()) {
                try {
                    FileUtils.deleteDirectory(sourceCacheFolder);
                } catch (IOException e) {
                    log.error("Error in removing source-cache", e);
                }
            }
        }

        Storage storage = null;
        if(storageType == null || storageType.trim().length() == 0 || storageType.equals("JDBC")) {
            if(databaseDriver == null || databaseUrl == null || databaseUsername ==null || databasePassword == null || databaseHibernateDialect == null) {
                log.error("JDBC parameters are not properly set (All -jdbc parameters are required). Terminating...");
                jCommander.usage();
                return;
            }
            storage = new JDBCStorage(databaseDriver, databaseUrl, databaseUsername, databasePassword.toCharArray(), databaseHibernateDialect);
        } else {
            log.error("No valid storage option selected. Terminating...");
            jCommander.usage();
            return;
        }

        try {
            ProductScanner scanner = new DefaultProductScanner();
            scanner.scan(storage);
        } catch (Exception e) {
            log.fatal("Exception occured during scanning process. Terminating...", e);
        } finally {
            storage.close();
        }
    }
}