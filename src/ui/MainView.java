package ui;

import javax.swing.*;
import java.awt.*;
import db.ConnectionManager;
import db.DBConnection;

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

        // Panel de botones
        JPanel panelBotones = new JPanel();

        JButton btnCrearTabla = new JButton("Crear Tabla");
        btnCrearTabla.addActionListener(e -> abrirCrearTabla());

        panelBotones.add(btnCrearTabla);

        // Agregar panel de botones arriba de la ventana
        add(panelBotones, BorderLayout.NORTH);
    }

    private void abrirCrearTabla() {
    String selectedName = connectionList.getSelectedValue();
    if (selectedName != null) {
        DBConnection db = connectionManager.getConnection(selectedName);
        CrearTablaView crearTabla = new CrearTablaView(db);
        crearTabla.setVisible(true);
    } else {
        JOptionPane.showMessageDialog(this,
                "Seleccione una conexión primero.",
                "Atención",
                JOptionPane.WARNING_MESSAGE);
    }
}

}
