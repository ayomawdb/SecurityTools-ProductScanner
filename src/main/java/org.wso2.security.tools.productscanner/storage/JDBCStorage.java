package org.wso2.security.tools.productscanner.storage;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.wso2.security.tools.productscanner.AppConfig;
import org.wso2.security.tools.productscanner.pojo.*;

import java.util.List;
import java.util.Properties;

/**
 * Created by ayoma on 4/13/17.
 */
public class JDBCStorage implements Storage {
    private static Logger log = Logger.getLogger(JDBCStorage.class.getName());
    private static SessionFactory sessionFactory;

    public JDBCStorage(String driverName, String connectionUri, String username, char[] password, String hibernateDialect) {
        try{
            StandardServiceRegistryBuilder registryBuilder =  new StandardServiceRegistryBuilder();

            Properties properties = new Properties();
            properties.put("hibernate.connection.driver_class", driverName);
            properties.put("hibernate.connection.url", connectionUri);
            properties.put("hibernate.connection.username", username);
            properties.put("hibernate.connection.password", new String(password));
            properties.put("hibernate.dialect", hibernateDialect);
            if(AppConfig.isCreateDB()) {
                properties.put("hibernate.hbm2ddl.auto", "create");
            }
            if(AppConfig.isDebug()) {
                properties.put("hibernate.show_sql", "true");
                properties.put("hibernate.format_sql", "true");
            }

            Configuration configuration = new Configuration();
            configuration.addProperties(properties);

            configuration.addAnnotatedClass(Product.class);
            configuration.addAnnotatedClass(ProductArtifact.class);
            configuration.addAnnotatedClass(ProductDeploymentArtifact.class);
            configuration.addAnnotatedClass(Repo.class);
            configuration.addAnnotatedClass(RepoArtifact.class);

            sessionFactory = configuration.buildSessionFactory();

            for(int i = 0; i < password.length; i++) {
                password[i] = ' ';
            }
        }catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void close() {
        sessionFactory.close();
    }

    @Override
    public RepoArtifact searchFinalName(String finalName1) {
        Session session = sessionFactory.openSession();
        List results = null;
        try {
            String hql = "FROM org.wso2.security.tools.productscanner.pojo.RepoArtifact RA WHERE RA.finalName = :finalName";
            Query query = session.createQuery(hql);
            query.setParameter("finalName", finalName1);
            results = query.list();
            if (results.size() > 1) {
                log.warn("[Unexpected] Unexpected condition. File name " + finalName1 + " found multiple times");
            }
        } finally {
            session.close();
        }
        return (results == null || results.isEmpty()) ? null : (RepoArtifact) results.get(0);
    }

    @Override
    public boolean persist(ProductArtifact productArtifact) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            List<Product> productList = getProductList(productArtifact.getProduct().getFileName());
            if (productList.isEmpty()) {
                session.save(productArtifact.getProduct());
            } else {
                productArtifact.setProduct(productList.get(0));
            }
            session.save(productArtifact);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
        return true;
    }

    private List<Product> getProductList(String fileName) {
        Session session = sessionFactory.openSession();
        List results = null;
        try {
            String hql = "FROM org.wso2.security.tools.productscanner.pojo.Product P WHERE P.fileName = :fileName";
            Query query = session.createQuery(hql);
            query.setParameter("fileName", fileName);
            results = query.list();
            if (results.size() > 1) {
                log.warn("[Unexpected] Unexpected condition. File name " + fileName + " found multiple times");
            }
        } finally {
            session.close();
        }
        return results;
    }

    @Override
    public boolean persist(ProductDeploymentArtifact productDeploymentArtifact) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            List<Product> productList = getProductList(productDeploymentArtifact.getProduct().getFileName());
            if (productList.isEmpty()) {
                session.save(productDeploymentArtifact.getProduct());
            } else {
                productDeploymentArtifact.setProduct(productList.get(0));
            }
            session.save(productDeploymentArtifact);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
        return true;
    }
}
