package com.example.devoirthreads;

import java.sql.*;

public class DatabaseConnection {
    // Database configuration parameters
    private static final String URL = "jdbc:mysql://localhost:3306/devoir";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Statically load the driver
    static {
        try {
            // Use DriverManager to automatically load the driver
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du pilote MySQL : " + e.getMessage());
            throw new RuntimeException("Impossible de charger le pilote MySQL", e);
        }
    }

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    // Constructeur privé
    private DatabaseConnection() {}

    // Méthode d'obtention de l'instance singleton
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }
    public static boolean isCustomerExists(Connection conn, int customerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM customer WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    public void insert2order(int id ,Date date, double amount, int custid) {
        String query = "insert into orders(id,date,amount,customer_id,status) values (?, ?, ?, ?)";

        try(PreparedStatement statement= connection.prepareStatement(query)){
            statement.setInt(1, id);
            statement.setDate(2, date);
            statement.setDouble(3, amount);
            statement.setInt(4, custid);

            int result =statement.executeUpdate();
            if(result >0){
                System.out.println("Insertion has been successful");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        try {
            // Exemple d'utilisation
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            Connection conn = dbConnection.getConnection();

            ResultSet rs = dbConnection.executeQuery("SELECT * FROM customer");
            while (rs.next()) {
                System.out.println(rs.getString("nom"));
            }

            // Fermeture de la connexion
            dbConnection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();}
    }
}