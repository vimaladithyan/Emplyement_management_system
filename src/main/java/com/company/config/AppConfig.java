package com.company.config;

import com.company.util.EmployeeIdGenerator;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan("com.company")
@EnableTransactionManagement
@PropertySource("application.properties")
public class AppConfig {

    // --- @Bean: DataSource -------------------------------------------------
    @Bean
    public DataSource dataSource(
            @Value("${db.url}") String url,
            @Value("${db.username}") String username,
            @Value("${db.password}") String password,
            @Value("${db.driver}") String driverClassName) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        return ds;
    }

    // --- @Bean: EntityManagerFactory (configured manually, no Boot) --------
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            @Value("${hibernate.show_sql:true}") boolean showSql,
            @Value("${hibernate.hbm2ddl:update}") String hbm2ddl,
            @Value("${hibernate.dialect:}") String dialect) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.company.entity");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setShowSql(showSql);
        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.hbm2ddl.auto", hbm2ddl);
        if (dialect != null && !dialect.isBlank()) {
            jpaProps.put("hibernate.dialect", dialect);
        }
        factory.setJpaProperties(jpaProps);
        factory.setJpaVendorAdapter(adapter);
        return factory;
    }

    // --- @Bean: transaction manager ---------------------------------------
     @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    // Enables @PersistenceContext injection of EntityManager.
    @Bean
    public static PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
        return new PersistenceAnnotationBeanPostProcessor();
    }

    // --- @Bean + @Scope("prototype"): custom bean (PRD §10 util) -----------
    // Prototype: every getBean(EmployeeIdGenerator.class) returns a NEW
    // instance. Its @PostConstruct runs, but @PreDestroy is NOT called
    // by the container for prototype beans (documented behaviour).
    @Bean
    @Scope("prototype")
    public EmployeeIdGenerator employeeIdGenerator(
            @Value("${notification.email.from:noreply@company.local}") String emailFrom) {
        return new EmployeeIdGenerator(emailFrom);
    }

    // Singleton demo bean: default @Scope("singleton") — the same instance
    // is shared everywhere it is injected / looked up (see Main option 8).
    @Bean
    public String applicationName(@Value("${notification.sms.sender:COMPANY}") String sender) {
        return "Employee Management System (sender=" + sender + ")";
    }
}
