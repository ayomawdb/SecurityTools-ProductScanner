package org.wso2.security.tools.productscanner.pojo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by ayoma on 4/17/17.
 */
@Entity
@Table(name = "PRODUCT", indexes = { @Index(columnList = "NAME, VERSION", name = "product_name_version_idx"), @Index(columnList = "FILE_NAME" , name="product_file_name_idx") })
public class Product {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy= GenerationType.AUTO, generator="product_seq_gen")
    @SequenceGenerator(name="product_seq_gen", sequenceName="PRODUCT_SEQ")
    private Long id;

    @Column(name = "FILE_NAME", nullable = false, unique = true)
    private String fileName;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "VERSION", nullable = false)
    private String version;

    @Column(name = "CARBON_VERSION_TEXT", nullable = false)
    private String carbonVersionText;

    @Column(name = "VERSION_TEXT", nullable = false)
    private String versionText;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ADDED_DATE")
    private Date addedDate;

    public Product() {
    }

    public Product(String fileName, String name, String version, String carbonVersionText, String versionText) {
        this.fileName = fileName;
        this.name = name;
        this.version = version;
        this.carbonVersionText = carbonVersionText;
        this.versionText = versionText;
        this.addedDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCarbonVersionText() {
        return carbonVersionText;
    }

    public void setCarbonVersionText(String carbonVerionText) {
        this.carbonVersionText = carbonVerionText;
    }

    public String getVersionText() {
        return versionText;
    }

    public void setVersionText(String versionText) {
        this.versionText = versionText;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", carbonVersionText='" + carbonVersionText + '\'' +
                ", versionText='" + versionText + '\'' +
                ", addedDate=" + addedDate +
                '}';
    }
}
