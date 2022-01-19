package web.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:DataBase.properties")
@EnableTransactionManagement
@ComponentScan(value = "web")
public class DataConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("DataBase.driver"));
        dataSource.setUrl(env.getProperty("DataBase.url"));
        dataSource.setUsername(env.getProperty("DataBase.username"));
        dataSource.setPassword(env.getProperty("DataBase.password"));

        return dataSource;
    }

    @Bean
    public FactoryBean<EntityManagerFactory> entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean containerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        containerEntityManagerFactoryBean.setDataSource(getDataSource());

        HibernateJpaVendorAdapter adaptor = new HibernateJpaVendorAdapter();
        containerEntityManagerFactoryBean.setJpaVendorAdapter(adaptor);

        Properties props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", "update");
        props.setProperty("DataBase.driver", "com.mysql.cj.jdbc.Driver");
        props.setProperty("hibernate.show_sql","true");
        props.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.connection.characterEncoding", "utf8");
        props.setProperty("hibernate.connection.CharSet", "utf8");
        props.setProperty("hibernate.connection.useUnicode", "true");

        containerEntityManagerFactoryBean.setJpaProperties(props);
        containerEntityManagerFactoryBean.setPackagesToScan(env.getProperty("DataBase.entity.package"));

        return containerEntityManagerFactoryBean;
    }

    @Bean
    public TransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setDataSource(getDataSource());
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

}
