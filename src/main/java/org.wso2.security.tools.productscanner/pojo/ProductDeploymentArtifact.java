package org.wso2.security.tools.productscanner.pojo;

import javax.persistence.*;

/**
 * Created by ayoma on 4/18/17.
 */
@Entity
@Table(name = "PRODUCT_DEPLOYMENT_ARTIFACT")
public class ProductDeploymentArtifact {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy= GenerationType.AUTO, generator="product_deployment_artifact_seq_gen")
    @SequenceGenerator(name="product_deployment_artifact_seq_gen", sequenceName="PRODUCT_DEPLOYMENT_ARTIFACT_SEQ")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "PATH", nullable = false, length = 2048)
    private String path;

    public ProductDeploymentArtifact(Product product, String path) {
        this.product = product;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ProductDeploymentArtifact{" +
                "id=" + id +
                ", product=" + product +
                ", path='" + path + '\'' +
                '}';
    }
}
