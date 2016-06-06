package it.scrs.miner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("it.scrs.miner.dao")
@EntityScan("it.scrs.miner.dao")
public class PersistenceJPAConfig {

	@Value("${jpa.database.url}")
	private String database;

    @Value("${jpa.database.driverClass}")
	private String driverClass;

    @Value("${jpa.database.username}")
	private String username;

    @Value("${jpa.database.password}")
	private String password;

    @Value("${jpa.database.name}")
    private String dbName;


	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		System.err.println(database + " " + driverClass + " " + username + " " + password + " ");

        Properties properties = additionalProperties();

        Connection connection = null;
        // Sporcheria explaination:
        // Creo il database da zero, se fallisce allora
        // Aggiorno le proprietà
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=" + username + "&password=" + password + "&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
            Statement stmt = connection.createStatement();
		    stmt.executeUpdate("CREATE DATABASE " + dbName);
            System.out.println("Database creato con successo.");
        } catch (SQLException e) {
            System.out.println("Database già presente, aggiorno le proprietà.");
            properties.remove("hibernate.hbm2ddl.auto");
            properties.setProperty("hibernate.hbm2ddl.auto", "update");
            // e.printStackTrace();
        }

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] {"it.scrs.miner", "it.scrs.miner.models"});
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(properties);
		return em;
	}

	@Bean
	public DataSource dataSource(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(database);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "create");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		properties.setProperty("hibernate.show_sql", "true");
		return properties;
	}
}