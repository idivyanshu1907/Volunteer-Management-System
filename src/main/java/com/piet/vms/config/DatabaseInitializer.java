package com.piet.vms.config;

import com.piet.vms.model.Role;
import com.piet.vms.security.PasswordUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.stream.Collectors;

public final class DatabaseInitializer {
    private DatabaseInitializer() {}

    public static void initialize() throws Exception {
        try (Connection server = Database.getServerConnection(); Statement st = server.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS volunteer_management_vms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        try (Connection c = Database.getConnection()) {
            String ddl;
            try (var in = DatabaseInitializer.class.getResourceAsStream("/db/schema.sql")) {
                if (in == null) throw new IllegalStateException("Missing /db/schema.sql resource");
                try (var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) { ddl = reader.lines().collect(Collectors.joining("\n")); }
            }
            for (String statement : ddl.split(";")) {
                String sql = statement.trim();
                if (!sql.isEmpty()) try (Statement st = c.createStatement()) { st.execute(sql); }
            }
            seed(c);
        }
    }

    private static void seed(Connection c) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) { rs.next(); if (rs.getInt(1) > 0) return; }
        try (PreparedStatement p = c.prepareStatement("INSERT IGNORE INTO departments(department_name) VALUES (?), (?), (?)")) {
            p.setString(1, "Artificial Intelligence & Data Science"); p.setString(2, "Computer Science & Engineering"); p.setString(3, "Electrical Engineering"); p.executeUpdate();
        }
        long aid = departmentId(c, "Artificial Intelligence & Data Science");
        long cse = departmentId(c, "Computer Science & Engineering");
        insertUser(c, "Dr. Budesh Kanwer", null, "hod@piet.local", "HOD", aid);
        insertUser(c, "Ashok Coudhary", null, "proctor@piet.local", "PROCTOR", aid);
        insertUser(c, "Divyanshu Soni", "PIET25CA014", "student@piet.local", "STUDENT", cse);
        insertUser(c, "Akshat Agal", "PIET25CA005", "student2@piet.local", "STUDENT", cse);
        insertUser(c, "Paras Agarwal", null, "host@piet.local", "HOST", aid);
        try (PreparedStatement p = c.prepareStatement("INSERT INTO events(title,description,event_date,event_time,venue,volunteers_required,registration_deadline,reward,host_id,hod_id,status) VALUES (?,?,?,?,?,?,?,?,?,?, 'APPROVED')")) {
            p.setString(1, "Clean Campus Drive"); p.setString(2, "Help keep the campus clean and welcoming."); p.setDate(3, Date.valueOf(java.time.LocalDate.now().plusDays(14))); p.setTime(4, Time.valueOf("09:00:00")); p.setString(5, "College Main Campus"); p.setInt(6, 20); p.setDate(7, Date.valueOf(java.time.LocalDate.now().plusDays(10))); p.setString(8, "Certificate of Participation"); p.setLong(9, userId(c, "host@piet.local")); p.setLong(10, userId(c, "hod@piet.local")); p.executeUpdate();
        }
    }
    private static long departmentId(Connection c, String name) throws SQLException { try (PreparedStatement p=c.prepareStatement("SELECT department_id FROM departments WHERE department_name=?")){p.setString(1,name);try(ResultSet r=p.executeQuery()){r.next();return r.getLong(1);}} }
    private static long userId(Connection c, String email) throws SQLException { try (PreparedStatement p=c.prepareStatement("SELECT user_id FROM users WHERE email=?")){p.setString(1,email);try(ResultSet r=p.executeQuery()){r.next();return r.getLong(1);}} }
    private static void insertUser(Connection c, String name, String reg, String email, String role, long dept) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO users(full_name,registration_number,email,password_hash,role,department_id) VALUES(?,?,?,?,?,?)")) {
            p.setString(1,name); p.setString(2,reg); p.setString(3,email); p.setString(4, PasswordUtil.hash("Welcome123!".toCharArray())); p.setString(5,role); p.setLong(6,dept); p.executeUpdate();
        }
    }
}
