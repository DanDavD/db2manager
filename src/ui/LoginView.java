package ui;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField txtHost;
    private JTextField txtPort;
    private JTextField txtDatabase;
    private JTextField txtUser;
    private JPasswordField txtPassword;
    private JButton btnConnect;

    public LoginView() {
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
        txtPort = new JTextField("50000");
        panel.add(txtPort);

        panel.add(new JLabel("Database:"));
        txtDatabase = new JTextField();
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
        String port = txtPort.getText();
        String db = txtDatabase.getText();
        String user = txtUser.getText();

        JOptionPane.showMessageDialog(this,
                "Simulated connection to:\n" +
                host + ":" + port + "/" + db +
                "\nUser: " + user);
    }
}
