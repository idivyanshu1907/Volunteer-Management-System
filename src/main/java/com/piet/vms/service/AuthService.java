package com.piet.vms.service;

import com.piet.vms.config.Database;
import com.piet.vms.model.Role;
import com.piet.vms.model.User;
import com.piet.vms.security.PasswordUtil;

import java.sql.*;

public final class AuthService {
    public User login(String email, char[] password, Role selectedRole) {
        if (email == null || email.isBlank() || password == null || password.length == 0 || selectedRole == null)
            throw new ServiceException("Enter your email, password and role.");
        String sql = "SELECT user_id,full_name,registration_number,email,password_hash,role,department_id FROM users WHERE LOWER(email)=LOWER(?) AND active=TRUE";
        try (Connection c=Database.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,email.trim());
            try(ResultSet r=p.executeQuery()) {
                if (!r.next() || !PasswordUtil.verify(password,r.getString("password_hash"))) throw new ServiceException("Email or password is incorrect.");
                Role actual;
                try { actual=Role.valueOf(r.getString("role")); } catch(Exception e) { throw new ServiceException("This account has an invalid role."); }
                if (actual != selectedRole) throw new ServiceException("This account belongs to the " + actual.label() + " role. Select the correct role.");
                long dept=r.getLong("department_id"); Long department=r.wasNull()?null:dept;
                return new User(r.getLong("user_id"),r.getString("full_name"),r.getString("registration_number"),r.getString("email"),actual,department);
            }
        } catch(SQLException e) { throw new ServiceException("Cannot connect to the database. Check MySQL and database settings.",e); }
        finally { if(password!=null) java.util.Arrays.fill(password,'\0'); }
    }
}
