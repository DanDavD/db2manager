package ui;

import javax.swing.*;
import db.DBConnection;
import java.awt.*;
import java.sql.Connection;

import db.ConnectionManager;

public class LoginView extends JFrame {

    private JTextField txtHost;
    private JTextField txtPort;
    private JTextField txtDatabase;
    private JTextField txtUser;
    private JPasswordField txtPassword;
    private JButton btnConnect;
    private ConnectionManager connectionManager;

    public LoginView() {
        connectionManager = new ConnectionManager();
        setTitle("DB2 Manager - Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

        panel.add(new JLabel("Host:"));
        txtHost = new JTextField("localhost");
        panel.add(txtHost);

        panel.add(new JLabel("Port:"));
        txtPort = new JTextField("25000");
        panel.add(txtPort);

        panel.add(new JLabel("Database:"));
        txtDatabase = new JTextField("TESTDB"); // Puedes poner TESTDB por default
        panel.add(txtDatabase);

        panel.add(new JLabel("User:"));
        txtUser = new JTextField();
        panel.add(txtUser);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        btnConnect = new JButton("Connect");
        panel.add(new JLabel());
        panel.add(btnConnect);

        add(panel);

        btnConnect.addActionListener(e -> simulateConnection());
    }

    private void simulateConnection() {
        String host = txtHost.getText();
        String db = txtDatabase.getText();
        String user = txtUser.getText();
        String password = new String(txtPassword.getPassword());

        // Convertir puerto a entero
        int port;
        try {
            port = Integer.parseInt(txtPort.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El puerto debe ser un número válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar campos obligatorios
        if (host.isEmpty() || db.isEmpty() || user.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

       
        try {
            // Conectar usando host, puerto, database, usuario y contraseña
            connectionManager.addConnection(
        db + "_" + user,  // nombre identificador
        host,
        port,
        db,
        user,
        password
);

String connectionName = db + "_" + user;
DBConnection connection = connectionManager.getConnection(connectionName);


            JOptionPane.showMessageDialog(this, "Conectado correctamente a DB2!");
            new MainView(connectionManager);
            dispose(); // cerrar ventana de login


            // Listar primeras 10 tablas (opcional)
            var stmt = connection.getConnection().createStatement();
            var rs = stmt.executeQuery("SELECT TABNAME FROM SYSCAT.TABLES FETCH FIRST 10 ROWS ONLY");

            StringBuilder tables = new StringBuilder("Primeras tablas en DB2:\n");
            while (rs.next()) {
                tables.append(rs.getString("TABNAME")).append("\n");
            }
            JOptionPane.showMessageDialog(this, tables.toString());

            

        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "Driver JDBC no encontrado:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } 
    }
}
