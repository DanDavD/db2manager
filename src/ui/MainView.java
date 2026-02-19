package ui;

import javax.swing.*;
import java.awt.*;
import db.ConnectionManager;

public class MainView extends JFrame {

    private ConnectionManager connectionManager;
    private JList<String> connectionList;
    private DefaultListModel<String> listModel;

    public MainView(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;

        setTitle("DB2 Manager - Main");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        listModel = new DefaultListModel<>();

        // Llenar lista con conexiones activas
        String[] connections = connectionManager.listConnectionNames();
        for (String name : connections) {
            listModel.addElement(name);
        }

        connectionList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(connectionList);

        add(scrollPane, BorderLayout.WEST);
    }
}
