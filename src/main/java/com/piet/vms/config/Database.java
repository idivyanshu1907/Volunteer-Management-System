package com.piet.vms.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private Database() {}
    public static String url() { return System.getProperty("vms.db.url", env("VMS_DB_URL", "jdbc:mysql://localhost:3306/volunteer_management_vms?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata")); }
    public static String user() { return System.getProperty("vms.db.user", env("VMS_DB_USER", "root")); }
    public static String password() { return System.getProperty("vms.db.password", env("VMS_DB_PASSWORD", "")); }
    private static String env(String name, String fallback) { String v = System.getenv(name); return v == null ? fallback : v; }
    public static Connection getConnection() throws SQLException { return DriverManager.getConnection(url(), user(), password()); }
    public static Connection getServerConnection() throws SQLException {
        String u=url();
        int q=u.indexOf('?'); String suffix=q>=0?u.substring(q):""; String base=q>=0?u.substring(0,q):u;
        int slash=base.lastIndexOf('/');
        if(slash>"jdbc:mysql://".length()) base=base.substring(0,slash+1);
        return DriverManager.getConnection(base+suffix,user(),password());
    }
    public static void testConnection() throws SQLException { try (Connection ignored = getConnection()) { } }
}
