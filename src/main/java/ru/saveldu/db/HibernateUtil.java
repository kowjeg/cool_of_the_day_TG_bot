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

            String connectString = "jdbc:mysql://" + System.getenv("HOST_NAME") + ":"
                    + System.getenv("DB_PORT") + "/";
            settings.put("hibernate.connection.url", String.format(
                    "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                    System.getenv("HOST_NAME"),
                    System.getenv("DB_PORT"),
                    System.getenv("DB_NAME")
            ));
            settings.put("hibernate.connection.username", System.getenv("DB_USER"));
            settings.put("hibernate.connection.password", System.getenv("DB_PASSWORD"));
            settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
            settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            settings.put("hibernate.show_sql", "true");
            settings.put("hibernate.format_sql", "true");
            settings.put("hibernate.hbm2ddl.auto", "update");

            configuration.setProperties(settings);

            configuration.addAnnotatedClass(ru.saveldu.entities.User.class);


            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + ex);
        }
    }

    // Метод для получения SessionFactory
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Метод для закрытия SessionFactory (например, при завершении работы приложения)
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
