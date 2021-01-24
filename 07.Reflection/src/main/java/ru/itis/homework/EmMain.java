package ru.itis.homework;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class EmMain {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("src/main/resources/db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.getProperty("db.jdbc.url"));
        hikariConfig.setUsername(properties.getProperty("db.jdbc.user"));
        hikariConfig.setPassword(properties.getProperty("db.jdbc.password"));
        hikariConfig.setDriverClassName(properties.getProperty("db.jdbc.driver.classname"));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.jdbc.hikari.max-pool-size")));

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        try {
            System.out.println(dataSource.getConnection().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        EntityManager entityManager = new EntityManager(dataSource);
        //entityManager.createTable("account", User.class);
//        User Zhanna = new User();
//        Zhanna.setFirstName("user1");
//        Zhanna.setId(2L);
//        Zhanna.setLastName("user1Lastname");
//        Zhanna.setWorker(false);
//        entityManager.save("account", Zhanna);
        entityManager.findById("account",User.class, Long.class,2L);

    }

}
