package com.smart.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class InitWangwu {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/smart_manager_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false";
        String user = "root";
        String password = "password"; // from application.yml

        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {

            // check if wangwu exists
            ResultSet rs = stmt.executeQuery("SELECT * FROM sys_user WHERE username = 'wangwu'");
            if (rs.next()) {
                System.out.println("wangwu user already exists!");
                return;
            }

            // Insert wangwu
            String insertUser = "INSERT INTO sys_user (username, password, real_name, dept_id, status) VALUES " +
                    "('wangwu', '$2a$10$7JB720yubVSZv5x8vlmZ9.n9Jdkp/Jr/qRzLg.g34.t7q/J5u2Z.', '王护士', 4, 1)";
            stmt.executeUpdate(insertUser);

            // get ids
            ResultSet rsUser = stmt.executeQuery("SELECT id FROM sys_user WHERE username = 'wangwu'");
            rsUser.next();
            long userId = rsUser.getLong(1);

            ResultSet rsRole = stmt.executeQuery("SELECT id FROM sys_role WHERE role_key = 'common'");
            long roleId = 2; // default
            if (rsRole.next()) {
                roleId = rsRole.getLong(1);
            }

            String insertRole = "INSERT INTO sys_user_role (user_id, role_id) VALUES (" + userId + ", " + roleId + ")";
            stmt.executeUpdate(insertRole);

            System.out.println("wangwu user inserted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
