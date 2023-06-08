package com.company;

import javax.swing.*;
import java.awt.*;
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
                    setVisible(false);
                    openNewWindow();
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
    private void openNewWindow() throws SQLException {
        JFrame resultFrame = new JFrame("Результаты запроса");
        resultFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String query = "SELECT id_rieltor, family FROM rieltors"; // Замените "rieltors" на имя вашей таблицы
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        JPanel resultPanel = new JPanel(new GridLayout(0, 2)); // Используем GridLayout для отображения данных в виде таблицы

        // Добавление заголовков столбцов в панель
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            resultPanel.add(new JLabel(columnName));
        }

        // Добавление данных в панель
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                Object value = resultSet.getObject(i);
                resultPanel.add(new JLabel(value.toString()));
            }
        }

        resultSet.close();
        statement.close();

        // Добавление строки ввода
        JTextField inputField = new JTextField();
        resultPanel.add(inputField);

        // Добавление кнопки "Выбрать"
        JButton selectButton = new JButton("Выбрать");
        JLabel greetingLabel = new JLabel("");
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedValue = inputField.getText();
                // Действия при выборе значения
                try {
                    int selectedId = Integer.parseInt(selectedValue);
                    String query = "SELECT family FROM rieltors WHERE id_rieltor = " + selectedId;
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    if (resultSet.next()) {
                        String family = resultSet.getString("family");
                        greetingLabel.setText("Привет, " + family);
                        openHouseWindow(selectedId);
                    } else {
                        JOptionPane.showMessageDialog(resultFrame,"Не найдено риелтора с указанным ID");
                    }

                    resultSet.close();
                    statement.close();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(resultFrame, "Некорректный номер");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(resultFrame, "Ошибка при выполнении запроса");
                }

                // Очистка данных
                inputField.setText("");

            }
        });
        resultPanel.add(selectButton);
        resultPanel.add(greetingLabel);
        resultFrame.getContentPane().add(resultPanel); // Добавление панели с результатами в окно
        resultFrame.pack(); // Установка оптимального размера окна на основе содержимого
        resultFrame.setVisible(true);

    }

    private void openHouseWindow(int selectedId) throws SQLException {
        JFrame houseFrame = new JFrame("Дома");
        try {
            String query = "SELECT id_hata, id_client, address FROM hata WHERE id_rieltor = " + selectedId;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Создание панели для отображения таблицы
            JPanel housePanel = new JPanel(new GridLayout(0, 3));
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Добавление заголовков столбцов в панель
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                housePanel.add(new JLabel(columnName));
            }

            // Добавление данных в панель
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    housePanel.add(new JLabel(value.toString()));
                }
            }

            resultSet.close();
            statement.close();

            houseFrame.getContentPane().add(housePanel); // Добавление панели с результатами в окно
            houseFrame.pack(); // Установка оптимального размера окна на основе содержимого
            houseFrame.setVisible(true);
        }
        catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Ошибка при выполнении запроса: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }


    }
