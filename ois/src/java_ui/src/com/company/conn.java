package com.company;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conn extends JFrame
{
    static PrintWriter pw = new PrintWriter(System.out, true);
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    public static Connection connection = null;

    private JPasswordField pf_pass;
    private JLabel lb_login;
    //private String url = "jdbc:mysql://localhost:3306/mysql";
    String url = "jdbc:mysql://localhost:3306/ois_db";
    conn()
    {
        super("Вход");
        JPanel pan = new JPanel();
        pan.setLayout(null);

        JLabel lb_login = new JLabel("Логин");
        lb_login.setLocation(60,20);
        lb_login.setSize(80,20);

        JTextField tf_login = new JTextField();
        tf_login.setLocation(120, 20);
        tf_login.setSize(120, 20);

        JLabel lb_pass = new JLabel("Пароль");
        lb_pass.setLocation(60,60);
        lb_pass.setSize(80,20);

        JPasswordField pf_pass = new JPasswordField();
        pf_pass.setLocation(120, 60);
        pf_pass.setSize(120, 20);

        JButton bt_conn = new JButton("Вход");
        bt_conn.setLocation(100, 100);
        bt_conn.setSize(100, 20);
        bt_conn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                 //   Class.forName("com.mysql.jdbc.Driver");
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    pw.println("Driver was installed");

                }
                catch (Exception ex)
                {
                    pw.println("ERROR: Driver wasn't installed");
                }

                try
                {
                   // pw.println(tf_login.getText());
                    connection = DriverManager.getConnection(url, tf_login.getText(), pf_pass.getText());

                    pw.println("Connection is open");
                }
                catch (Exception ex)
                {
                    pw.println("ERROR: Cannot open connection");
                }
            }
        });

        pan.add(lb_login);
        pan.add(tf_login);
        pan.add(lb_pass);
        pan.add(pf_pass);
        pan.add(bt_conn);

        setContentPane(pan);
        setLocation(850, 450);
        setSize(600,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}