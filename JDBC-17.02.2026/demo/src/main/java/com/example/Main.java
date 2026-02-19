package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        // -------------------------
        // FORM VALIDATION
        // -------------------------

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        if (!email.contains("@")) {
            System.out.println("Invalid email format!");
            return;
        }

        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters!");
            return;
        }

        // -------------------------
        // LOAD DATABASE PROPERTIES (Production Safe)
        // -------------------------

        Properties prop = new Properties();

        try (InputStream input = Main.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                System.out.println("Unable to find db.properties");
                return;
            }

            prop.load(input);

        } catch (Exception e) {
            System.out.println("Failed to load database configuration.");
            e.printStackTrace();
            return;
        }

        String url = prop.getProperty("db.url");
        String user = prop.getProperty("db.username");
        String dbPassword = prop.getProperty("db.password");

        // -------------------------
        // DATABASE INSERT
        // -------------------------

        String query = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";

        try (
                Connection con = DriverManager.getConnection(url, user, dbPassword);
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();

            System.out.println("User registered successfully!");

        } catch (SQLException e) {
            System.out.println("Database error occurred.");
            e.printStackTrace();
        }

        sc.close();
    }
}
