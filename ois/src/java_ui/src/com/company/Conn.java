package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class Conn extends JFrame {
    static PrintWriter pw = new PrintWriter(System.out, true);
//    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    public static Connection connection = null;

//    private JPasswordField pf_pass;
//    private JLabel lb_login;
    String url = "jdbc:mysql://localhost:3306/ois_db";

    public Conn() {
        super("Вход");
        JPanel pan = new JPanel();
        pan.setLayout(null);

        JLabel lb_login = new JLabel("Логин");
        lb_login.setLocation(60, 20);
        lb_login.setSize(80, 20);

        JTextField tf_login = new JTextField();
        tf_login.setLocation(120, 20);
        tf_login.setSize(120, 20);

        JLabel lb_pass = new JLabel("Пароль");
        lb_pass.setLocation(60, 60);
        lb_pass.setSize(80, 20);

        JPasswordField pf_pass = new JPasswordField();
        pf_pass.setLocation(120, 60);
        pf_pass.setSize(120, 20);

        JButton bt_conn = new JButton("Вход");
        bt_conn.setLocation(100, 100);
        bt_conn.setSize(100, 20);
        bt_conn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    pw.println("Driver was installed");
                } catch (Exception ex) {
                    pw.println("ERROR: Driver wasn't installed");
                }

                try {
                    connection = DriverManager.getConnection(url, tf_login.getText(), pf_pass.getText());
                    pw.println("Connection is open");
                    setVisible(false);
                    openNewWindow();
                } catch (Exception ex) {
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
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void openNewWindow() throws SQLException {
        JFrame resultFrame = new JFrame("Введи номер риелтора");
        resultFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String query = "SELECT id_rieltor, family FROM rieltors";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        JPanel resultPanel = new JPanel(new BorderLayout());

        DefaultTableModel tableModel = new DefaultTableModel();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            tableModel.addColumn(columnName);
        }

        while (resultSet.next()) {
            Object[] rowData = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowData[i - 1] = resultSet.getObject(i);
            }
            tableModel.addRow(rowData);
        }

        resultSet.close();
        statement.close();

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        JTextField inputField = new JTextField();
        resultPanel.add(inputField, BorderLayout.SOUTH);

        JButton selectButton = new JButton("Выбрать");
        JLabel greetingLabel = new JLabel("");
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedValue = inputField.getText();
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
                        JOptionPane.showMessageDialog(resultFrame, "Не найдено риелтора с указанным ID");
                    }

                    resultSet.close();
                    statement.close();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(resultFrame, "Некорректный номер");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(resultFrame, "Ошибка при выполнении запроса");
                }

                inputField.setText("");
            }
        });

        JButton addClientButton = new JButton("Добавить клиента");
        addClientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame addClientFrame = new JFrame("Добавить клиента");
                JPanel addClientPanel = new JPanel(new GridLayout(4, 2));

                addClientPanel.add(new JLabel("Фамилия:"));
                JTextField surnameField = new JTextField();
                addClientPanel.add(surnameField);

                addClientPanel.add(new JLabel("Имя:"));
                JTextField nameField = new JTextField();
                addClientPanel.add(nameField);

                addClientPanel.add(new JLabel("Прописка:"));
                JTextField propiskaField = new JTextField();
                addClientPanel.add(propiskaField);

                addClientPanel.add(new JLabel("Паспорт:"));
                JTextField pasportField = new JTextField();
                addClientPanel.add(pasportField);

                JButton saveClientButton = new JButton("Сохранить");
                saveClientButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String surname = surnameField.getText();
                            String name = nameField.getText();
                            String propiska = propiskaField.getText();
                            String pasport = pasportField.getText();

                            String insertQuery = "INSERT INTO client (surname, name, propiska, pasport) VALUES (?, ?, ?, ?)";
                            PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                            insertStatement.setString(1, surname);
                            insertStatement.setString(2, name);
                            insertStatement.setString(3, propiska);
                            insertStatement.setString(4, pasport);
                            insertStatement.executeUpdate();

                            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                            int clientId = -1;
                            if (generatedKeys.next()) {
                                clientId = generatedKeys.getInt(1);
                            }

                            JOptionPane.showMessageDialog(addClientFrame, "Клиент добавлен успешно\nclient_id: " + clientId + "\nФамилия: " + surname + "\nИмя: " + name);

                            insertStatement.close();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(addClientFrame, "Ошибка при выполнении запроса: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                addClientPanel.add(saveClientButton);

                addClientFrame.getContentPane().add(addClientPanel);
                addClientFrame.pack();
                addClientFrame.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectButton);
        buttonPanel.add(greetingLabel);
        buttonPanel.add(addClientButton);
        resultPanel.add(buttonPanel, BorderLayout.NORTH);

        resultFrame.getContentPane().add(resultPanel);
        resultFrame.pack();
        resultFrame.setVisible(true);
    }

    private void openHouseWindow(final int selectedId) throws SQLException {
        JFrame houseFrame = new JFrame("Дома");
        try {
            String query = "SELECT id_hata, id_client, id_rieltor, address, total_area, houseroom, rooms, floor, cold_water, hot_water, balcony, type FROM hata WHERE id_rieltor = " + selectedId;
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel tableModel = new DefaultTableModel();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                tableModel.addColumn(columnName);
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            resultSet.close();
            statement.close();

            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            houseFrame.getContentPane().add(scrollPane);
            houseFrame.pack();
            houseFrame.setSize(800, 600);
            houseFrame.setVisible(true);

            JButton addButton = new JButton("Добавить");
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame addHouseFrame = new JFrame("Добавить запись в Дома");
                    JPanel addHousePanel = new JPanel(new GridLayout(13, 2));

                    addHousePanel.add(new JLabel("id_client:"));
                    JTextField idClientField = new JTextField();
                    addHousePanel.add(idClientField);

                    addHousePanel.add(new JLabel("id_rieltor:"));
//                    JTextField idRieltorField = new JTextField();
                    JTextField idRieltorField = new JTextField(String.valueOf(selectedId));
                    addHousePanel.add(idRieltorField);

                    addHousePanel.add(new JLabel("address:"));
                    JTextField addressField = new JTextField();
                    addHousePanel.add(addressField);

                    addHousePanel.add(new JLabel("total_area:"));
                    JTextField totalAreaField = new JTextField();
                    addHousePanel.add(totalAreaField);

                    addHousePanel.add(new JLabel("houseroom:"));
                    JTextField houseroomField = new JTextField();
                    addHousePanel.add(houseroomField);

                    addHousePanel.add(new JLabel("rooms:"));
                    JTextField roomsField = new JTextField();
                    addHousePanel.add(roomsField);

                    addHousePanel.add(new JLabel("floor:"));
                    JTextField floorField = new JTextField();
                    addHousePanel.add(floorField);

                    addHousePanel.add(new JLabel("cold_water (true/false):"));
                    JTextField coldWaterField = new JTextField();
                    addHousePanel.add(coldWaterField);

                    addHousePanel.add(new JLabel("hot_water (true/false):"));
                    JTextField hotWaterField = new JTextField();
                    addHousePanel.add(hotWaterField);

                    addHousePanel.add(new JLabel("balcony (true/false):"));
                    JTextField balconyField = new JTextField();
                    addHousePanel.add(balconyField);

                    addHousePanel.add(new JLabel("type:"));
                    JTextField typeField = new JTextField();
                    addHousePanel.add(typeField);

                    JButton saveButton = new JButton("Сохранить");
                    saveButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            try {
//                                int idHata = Integer.parseInt(idHataField.getText());
                                int idClient = Integer.parseInt(idClientField.getText());
                                int idRieltor = Integer.parseInt(idRieltorField.getText());
                                String address = addressField.getText();
                                int totalArea = Integer.parseInt(totalAreaField.getText());
                                int houseroom = Integer.parseInt(houseroomField.getText());
                                int rooms = Integer.parseInt(roomsField.getText());
                                int floor = Integer.parseInt(floorField.getText());
                                boolean coldWater = Boolean.parseBoolean(coldWaterField.getText());
                                boolean hotWater = Boolean.parseBoolean(hotWaterField.getText());
                                boolean balcony = Boolean.parseBoolean(balconyField.getText());
                                String type = typeField.getText();

                                String insertQuery = "INSERT INTO hata (id_client, id_rieltor, address, total_area, houseroom, rooms, floor, cold_water, hot_water, balcony, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
//                                insertStatement.setInt(1, idHata);
                                insertStatement.setInt(1, idClient);
                                insertStatement.setInt(2, idRieltor);
                                insertStatement.setString(3, address);
                                insertStatement.setInt(4, totalArea);
                                insertStatement.setInt(5, houseroom);
                                insertStatement.setInt(6, rooms);
                                insertStatement.setInt(7, floor);
                                insertStatement.setBoolean(8, coldWater);
                                insertStatement.setBoolean(9, hotWater);
                                insertStatement.setBoolean(10, balcony);
                                insertStatement.setString(11, type);
                                insertStatement.executeUpdate();

                                JOptionPane.showMessageDialog(addHouseFrame, "Запись добавлена успешно");

                                insertStatement.close();
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(addHouseFrame, "Некорректные данные");
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(addHouseFrame, "Ошибка при выполнении запроса: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });



                    addHousePanel.add(saveButton);

                    addHouseFrame.getContentPane().add(addHousePanel);
                    addHouseFrame.pack();
                    addHouseFrame.setVisible(true);
                }
            });



            houseFrame.getContentPane().add(addButton, BorderLayout.NORTH);

            JButton deleteButton = new JButton("Удалить");
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int idHata = (int) table.getValueAt(selectedRow, 0);
                        int confirm = JOptionPane.showConfirmDialog(houseFrame, "Вы уверены, что хотите удалить выбранный дом?");
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                String deleteQuery = "DELETE FROM hata WHERE id_hata = ?";
                                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                                deleteStatement.setInt(1, idHata);
                                deleteStatement.executeUpdate();

                                JOptionPane.showMessageDialog(houseFrame, "Дом удален успешно");

                                deleteStatement.close();
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(houseFrame, "Ошибка при выполнении запроса: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(houseFrame, "Выберите дом для удаления");
                    }
                }
            });

//            .add(deleteButton);
            houseFrame.getContentPane().add(deleteButton, BorderLayout.SOUTH);
            houseFrame.revalidate();

        } catch (Exception e) {
            pw.println("ERROR: Cannot open Houses window");
        }


    }



    public static void main(String[] args) {
        Conn jf = new Conn();
        jf.setVisible(true);
    }
}
