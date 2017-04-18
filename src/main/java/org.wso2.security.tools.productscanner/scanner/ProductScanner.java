package org.wso2.security.tools.productscanner.scanner;

import org.wso2.security.tools.productscanner.storage.Storage;

/**
 * Created by ayoma on 4/17/17.
 */
public interface ProductScanner {
    public void scan(Storage storage) throws Exception;
}
