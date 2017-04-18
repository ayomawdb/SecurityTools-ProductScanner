package org.wso2.security.tools.productscanner.pojo;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ayoma on 4/17/17.
 */
@Entity
@Table(name = "PRODUCT_ARTIFACT", indexes = { @Index(columnList = "FILE_NAME", name = "product_artifact_file_name_idx") })
public class ProductArtifact {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy= GenerationType.AUTO, generator="product_artifact_seq_gen")
    @SequenceGenerator(name="product_artifact_seq_gen", sequenceName="PRODUCT_ARTIFACT_SEQ")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FILE_PATH", nullable = false, length = 2048)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "REPO_ARTIFACT_ID", nullable = true)
    private RepoArtifact repoArtifact;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ADDED_DATE")
    private Date addedDate;

    public ProductArtifact(Product product, String fileName, String filePath, RepoArtifact repoArtifact) {
        this.product = product;
        this.fileName = fileName;
        this.filePath = filePath;
        this.repoArtifact = repoArtifact;
        this.addedDate = new Date();
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public RepoArtifact getRepoArtifact() {
        return repoArtifact;
    }

    public void setRepoArtifact(RepoArtifact repoArtifact) {
        this.repoArtifact = repoArtifact;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    @Override
    public String toString() {
        return "ProductArtifact{" +
                "id=" + id +
                ", product=" + product +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", repoArtifact=" + repoArtifact +
                ", addedDate=" + addedDate +
                '}';
    }
}
