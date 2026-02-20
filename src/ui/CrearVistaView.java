package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CrearVistaView extends JFrame {

    private JTextField tfNombreVista;
    private JTextArea taSelectSQL;
    private JTextArea taDDL;
    private DBConnection dbConnection;

    public CrearVistaView(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
        initComponents();
    }

    private void initComponents() {
        setTitle("Crear Vista");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // 🔹 Panel superior (nombre vista)
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Nombre Vista:"));
        tfNombreVista = new JTextField(20);
        topPanel.add(tfNombreVista);
        panel.add(topPanel, BorderLayout.NORTH);

        // 🔹 Área para escribir SELECT SQL
        JPanel centerPanel = new JPanel(new BorderLayout());
        taSelectSQL = new JTextArea(10, 50);
        JScrollPane scrollSelect = new JScrollPane(taSelectSQL);
        centerPanel.add(new JLabel("Sentencia SELECT:"), BorderLayout.NORTH);
        centerPanel.add(scrollSelect, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // 🔹 Panel botones
        JPanel buttonPanel = new JPanel();

        JButton btnGenerar = new JButton("Generar DDL");
        JButton btnEjecutar = new JButton("Ejecutar DDL");

        buttonPanel.add(btnGenerar);
        buttonPanel.add(btnEjecutar);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 🔹 Área DDL
        taDDL = new JTextArea(5, 50);
        JScrollPane scrollDDL = new JScrollPane(taDDL);
        panel.add(scrollDDL, BorderLayout.EAST);

        add(panel);

        // Eventos
        btnGenerar.addActionListener(e -> generarDDL());
        btnEjecutar.addActionListener(e -> ejecutarDDL());
    }

    private void generarDDL() {
        String nombreVista = tfNombreVista.getText().trim();
        String selectSQL = taSelectSQL.getText().trim();

        if (nombreVista.isEmpty() || selectSQL.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Debe ingresar el nombre de la vista y la sentencia SELECT",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ddl = "CREATE VIEW " + nombreVista + " AS \n" + selectSQL + ";";
        taDDL.setText(ddl);
    }

    private void ejecutarDDL() {
        try {
            String ddl = taDDL.getText().trim();
            if (ddl.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Primero genere el DDL",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            dbConnection.ejecutarDDL(ddl);

            JOptionPane.showMessageDialog(this,
                    "Vista creada correctamente!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear la vista:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // En CrearVistaView
public void setNombreVista(String nombre) {
    tfNombreVista.setText(nombre);
}

public void setSelectSQL(String sql) {
    taSelectSQL.setText(sql);
}
}