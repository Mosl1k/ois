package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
public class JavaDriverManagerGetConnectionExample2 {
    public static void main(String args[]) throws ClassNotFoundException
    {
        String name,pass,url;
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            url="jdbc:mysql://localhost:3306/ois_db";
            name="rieltor";
            pass="";
            con = DriverManager.getConnection(url,name,pass);
            System.out.println("Connection created");
            con.close();
            System.out.println("Connection closed");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}