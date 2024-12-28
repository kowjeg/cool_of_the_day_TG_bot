//package ru.saveldu.db;
//
//import java.sql.*;
//import java.time.LocalDate;
//
//public class DatabaseService {
//
//    private Connection connection;
//    private boolean isTablesExist = false;
//
//    public DatabaseService() {
//        initializeDatabaseConnection();
//    }
//
//    private void initializeDatabaseConnection() {
//        try {
//            Thread.sleep(4000);
//
//            String connectString = "jdbc:mysql://" + System.getenv("HOST_NAME") + ":"
//                    + System.getenv("DB_PORT") + "/";
//            connection = DriverManager.getConnection(
//                    connectString,
//                    System.getenv("DB_USER"),
//                    System.getenv("DB_PASSWORD")
//            );
//            if (!isTablesExist) {
//                initializeDBSchemaTable();
//            }
//
//        } catch (SQLException | InterruptedException e) {
////            System.out.println("test");
//            throw new RuntimeException("Ошибка подключения к базе данных", e);
//        }
//    }
//
//    private void initializeDBSchemaTable() throws SQLException {
//        String createSchemaSQL = "CREATE SCHEMA IF NOT EXISTS telegram_bot;";
//        String useSchemaSQL = "USE telegram_bot;";
//        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS telegram_bot.users ("
//                + "chat_id BIGINT NOT NULL, "
//                + "user_id BIGINT NOT NULL, "
//                + "user_name VARCHAR(255) NOT NULL, "
//                + "comb_size INT NOT NULL, "
//                + "last_played_date date NOT NULL, "
//                + "PRIMARY KEY (chat_id, user_id)"
//                + ");";
//        String createStatsTableSQL = "CREATE TABLE IF NOT EXISTS telegram_bot.stats ("
//                + "chat_id BIGINT NOT NULL, "
//                + "user_id BIGINT NOT NULL, "
//                + "user_name VARCHAR(255) NOT NULL, "
//                + "year INT NOT NULL, "
//                + "count INT DEFAULT 1 NOT NULL, "
//                + "PRIMARY KEY (chat_id, user_id, year)"
//                + ");";
//        String createCoolOfTheDayTableSQL = "CREATE TABLE IF NOT EXISTS telegram_bot.cool_of_the_day ("
//                + "chat_id BIGINT NOT NULL, "
//                + "user_id BIGINT NOT NULL, "
//                + "user_name VARCHAR(255) NOT NULL, "
//                + "date DATE NOT NULL, "
//                + "PRIMARY KEY (chat_id, user_id, date)"
//                + ");";
//
//        Statement stmt = connection.createStatement();
//        stmt.executeUpdate(createSchemaSQL);
//        stmt.executeUpdate(useSchemaSQL);
//        stmt.executeUpdate(createUsersTableSQL);
//        stmt.executeUpdate(createStatsTableSQL);
//        stmt.executeUpdate(createCoolOfTheDayTableSQL);
//        isTablesExist = true;
//    }
//
//    public void ensureConnection() {
//        try {
//            if (connection == null || connection.isClosed()) {
//                initializeDatabaseConnection();
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Не удалось восстановить соединение с базой данных", e);
//        }
//    }
//
//    public Connection getConnection() {
//        return connection;
//    }
//
//    public boolean isTablesExist() {
//        return isTablesExist;
//    }
//}
