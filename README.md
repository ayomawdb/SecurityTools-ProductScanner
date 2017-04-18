# SecurityTools - ProductScanner

This tool can be used to create a database of artifacts available in a given product pack or a given set of product packs. Gathered information will include:
* Product Name
* Product Version
* Content of version.txt
* Content of wso2carbon-version.txt
* List of content from /reporitory/deployment/server folder
* All the "jar", "war" and "mar" files available in the product pack
* Source code reference of all the "jar", "war" and "mar" files available in the product pack

All the "jar", "war" and "mar" files available in product pack will be cross checked with the database created using https://github.com/ayomawdb/SecurityTools-RepoScanner . After cross-checking, source code reference for the paticular artifact will be stored.

**"-source" parameter can be used to download all the known source files, relevant to the components shipped with a given product.**

Note: This repository is under active development. 

## Support
### Product Families 
* WSO2 Carbon 4 Based Products
### Storage
* JDBC

## Build
```
mvn clean install 
```
## Usage 
Note: Passwords and OAuth2 Tokens are accepted as command line inputs.
```
-------------------------------------------------
-----                                       -----
-----            Product Scanner            -----
-----                                       -----
-------------------------------------------------
Usage: WSO2 Product Scanner [options]
  Options:
    -path
      Product ZIP file or location to find multiple product ZIP files
    -storage
      Storage used in storing final results (Options: JDBC) (Default: JDBC)
    -jdbc.driver
      Database driver class (Default: com.mysql.jdbc.Driver)
    -jdbc.url
      Database connection URL (Default: jdbc:mysql://localhost/RepoScanner)
    -jdbc.username
      Database username (Default: root)
    -jdbc.password
      Database password
    -jdbc.dialect
      Database Hibernate dialect (Default: org.hibernate.dialect.MySQLDialect)
    -verbose, -v
      Verbose output
      Default: false
    -debug, -d
      Verbose + Debug output for debugging requirements
      Default: false
    --help, -help, -?

    -jdbc.create
      Drop and create JDBC tables
      Default: false
    -source
      Download source code relevant to each product into a separate folder
      Default: false
```
## Usage Examples
Scan WSO2 Identity Server 5.3.0 product pack
```
java -jar ProductScanner-1.0-SNAPSHOT.jar -jdbc.password -jdbc.username MySQLUser -path /home/sample/wso2is-5.3.0.zip
```
Scan all the product packs available in /home/sample/packs folder
```
java -jar ProductScanner-1.0-SNAPSHOT.jar -jdbc.password -jdbc.username MySQLUser -path /home/sample/packs
```
Scan WSO2 Identity Server 5.3.0 product pack and download all known component source folders
```
java -jar ProductScanner-1.0-SNAPSHOT.jar -jdbc.password -jdbc.username MySQLUser -path /home/sample/wso2is-5.3.0.zip -source
```
