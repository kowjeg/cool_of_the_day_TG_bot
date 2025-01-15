package ru.saveldu.db;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {

            Configuration configuration = new Configuration();

            // Настройки Hibernate
            Properties settings = new Properties();

            settings.put("hibernate.connection.url", String.format(
                    "jdbc:p6spy:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    System.getenv("DB_HOST"),
                    System.getenv("DB_PORT"),
                    System.getenv("DB_NAME")
            ));
            System.out.println(settings.get("hibernate.connection.url"));
            settings.put("hibernate.connection.username", System.getenv("DB_USER"));
            settings.put("hibernate.connection.password", System.getenv("DB_PASS"));
            System.out.println(settings.get("hibernate.connection.username"));
            System.out.println(settings.get("hibernate.connection.password"));

            settings.put("hibernate.connection.driver_class", "com.p6spy.engine.spy.P6SpyDriver");
            settings.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");

            settings.put("hibernate.type", "true");

            settings.put("hibernate.hbm2ddl.auto", "update");


//            settings.put("hibernate.hikari.dataSourceClassName", "com.zaxxer.hikari.HikariDataSource");
            settings.put("hibernate.hikari.minimumIdle", "5");
            settings.put("hibernate.hikari.maximumPoolSize", "10");
            settings.put("hibernate.hikari.idleTimeout", "300000");
            settings.put("hibernate.hikari.connectionTimeout", "30000");
            settings.put("hibernate.hikari.maxLifetime", "1800000");
            settings.put("hibernate.hikari.connectionTestQuery", "SELECT 1");

            configuration.setProperties(settings);

            configuration.addAnnotatedClass(ru.saveldu.entities.User.class);
            configuration.addAnnotatedClass(ru.saveldu.entities.Stat.class);
            configuration.addAnnotatedClass(ru.saveldu.entities.CoolOfTheDay.class);


            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + ex);
        }
    }


    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
