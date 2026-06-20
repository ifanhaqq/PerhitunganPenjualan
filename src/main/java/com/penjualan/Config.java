package com.penjualan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Config {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/db_perhitungan_penjualan";
            String user = "root";
            String pwd = "root1234";
            return DriverManager.getConnection(url, user, pwd);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Error: " + e.getMessage());
            return null;
        }
    }
}
