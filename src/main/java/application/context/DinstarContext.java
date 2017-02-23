package application.context;

import application.DinstarAPI;
import net.mitrol.utils.log.MitrolLogger;
import net.mitrol.utils.log.MitrolLoggerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by santiago.barandiaran on 7/12/2016.
 */
@Configuration
@EnableJpaRepositories(basePackages = "application.persistance", entityManagerFactoryRef = "dinstarEntityManagerFactory", transactionManagerRef = "dinstarTransactionManager")
@EnableTransactionManagement
public class DinstarContext {
    private MitrolLogger logger = MitrolLoggerImpl.getLogger(DinstarContext.class);
    private static final String SQLITE_LOCATION = "C:/Users/santiago.barandiaran/dinstarDB";

    private DriverManagerDataSource dinstarDataSource;
    private EntityManagerFactory dinstarEntityManagerFactory;
    private JpaTransactionManager dinstarTransactionManager;

    static {
        try {
            System.out.println(System.getProperty("java.library.path"));
            System.loadLibrary("libdwgsms");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    @PostConstruct
    public void postConstruct() {
        setDatabaseProperties();
    }

    //region Private Methods
    private void setDatabaseProperties() {


        logger.info(String.format("------------------------ model: %s %s %s", System.getProperty("sun.arch.data.model"), System.getProperty("com.ibm.vm.bitmode"), System.getProperty("os.arch")));

        DinstarAPI.INSTANCE.dwg_stop_server();

        String databaseUrl = "jdbc:sqlite:" + SQLITE_LOCATION;
        //DataSource
        dinstarDataSource = new DriverManagerDataSource();
        dinstarDataSource.setDriverClassName("org.sqlite.JDBC");
        dinstarDataSource.setUrl(databaseUrl);

        //EntityManagerFactory
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("application.persistance");
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "application.persistance.SQLiteDialect");
        factory.setJpaProperties(jpaProperties);
        factory.setDataSource(dinstarDataSource);
        factory.afterPropertiesSet();
        dinstarEntityManagerFactory = factory.getObject();

        //JpaTransactionManager
        dinstarTransactionManager = new JpaTransactionManager();
        dinstarTransactionManager.setEntityManagerFactory(dinstarEntityManagerFactory);
    }
    //endregion

    @Bean
    DataSource dinstarDataSource() {
        return dinstarDataSource;
    }

    @Bean(name = "dinstarEntityManagerFactory")
    EntityManagerFactory dinstarEntityManagerFactory() {
        return dinstarEntityManagerFactory;
    }

    @Bean(name = "dinstarTransactionManager")
    JpaTransactionManager dinstarTransactionManager() {
        return dinstarTransactionManager;
    }

    /*@Bean
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".jsp");

        return viewResolver;
    }*/
}
