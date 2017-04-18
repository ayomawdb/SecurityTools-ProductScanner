package org.wso2.security.tools.productscanner.storage;

import org.wso2.security.tools.productscanner.pojo.ProductArtifact;
import org.wso2.security.tools.productscanner.pojo.ProductDeploymentArtifact;
import org.wso2.security.tools.productscanner.pojo.RepoArtifact;

/**
 * Created by ayoma on 4/13/17.
 */
public interface Storage {
    public void close();

    public RepoArtifact searchFinalName(String finalName1);

    public boolean persist(ProductArtifact productArtifact);

    public boolean persist(ProductDeploymentArtifact productDeploymentArtifact);
}
