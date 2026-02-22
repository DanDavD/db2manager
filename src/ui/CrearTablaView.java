package ui;

import model.Tabla;
import model.Columna;
import db.ConnectionManager;
import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CrearTablaView extends JFrame {

    private JTextField tfNombreTabla;
    private JTable tablaColumnas;
    private DefaultTableModel tableModel;
    private JTextArea taDDL;

    private DBConnection dbConnection;


    public CrearTablaView(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
        initComponents();
    }

    private void initComponents() {

        setTitle("Crear Tabla");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel panel = new JPanel(new BorderLayout());

        // 🔹 Panel superior (nombre tabla)
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Nombre Tabla:"));
        tfNombreTabla = new JTextField(20);
        topPanel.add(tfNombreTabla);

        panel.add(topPanel, BorderLayout.NORTH);

        // 🔹 Tabla columnas
        String[] columnas = {"Nombre", "Tipo", "PK", "Not Null", "Unique"};
        tableModel = new DefaultTableModel(columnas, 0);
        tablaColumnas = new JTable(tableModel);

        JScrollPane scrollTabla = new JScrollPane(tablaColumnas);
        panel.add(scrollTabla, BorderLayout.CENTER);

        // 🔹 Panel botones
        JPanel buttonPanel = new JPanel();

        JButton btnAgregar = new JButton("Agregar Columna");
        JButton btnEliminar = new JButton("Eliminar Columna");
        JButton btnGenerar = new JButton("Generar DDL");
        JButton btnEjecutar = new JButton("Ejecutar DDL");

        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnGenerar);
        buttonPanel.add(btnEjecutar);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 🔹 Área DDL
        taDDL = new JTextArea(5, 50);
        JScrollPane scrollDDL = new JScrollPane(taDDL);
        panel.add(scrollDDL, BorderLayout.EAST);

        add(panel);

        // Eventos
        btnAgregar.addActionListener(e -> agregarColumna());
        btnEliminar.addActionListener(e -> eliminarColumna());
        btnGenerar.addActionListener(e -> generarDDL());
        btnEjecutar.addActionListener(e -> ejecutarDDL());
    }

    private void agregarColumna() {
        JTextField tfNombre = new JTextField();
        JTextField tfTipo = new JTextField();
        JCheckBox cbPK = new JCheckBox("PK");
        JCheckBox cbNN = new JCheckBox("Not Null");
        JCheckBox cbUnique = new JCheckBox("Unique");

        cbPK.addActionListener(e -> {
        if (cbPK.isSelected()) {
            cbNN.setSelected(true); // Si es PK marca null
            cbNN.setEnabled(false);  //bloquear
        } else {
            cbNN.setEnabled(true);   // Si quita PK, deja que edite Not Null
        }
    });

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(tfNombre);
        panel.add(new JLabel("Tipo:"));
        panel.add(tfTipo);
        panel.add(cbPK);
        panel.add(cbNN);
        panel.add(cbUnique);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Agregar Columna", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            tableModel.addRow(new Object[]{
                    tfNombre.getText(),
                    tfTipo.getText(),
                    cbPK.isSelected(),
                    cbNN.isSelected(),
                    cbUnique.isSelected()
            });
        }
    }

    private void eliminarColumna() {
        int fila = tablaColumnas.getSelectedRow();
        if (fila != -1) {
            tableModel.removeRow(fila);
        }
    }

    private void generarDDL() {
        List<Columna> columnas = obtenerColumnas();
        Tabla tabla = new Tabla(tfNombreTabla.getText(), columnas);
        taDDL.setText(tabla.generarDDL());
    }

    private void ejecutarDDL() {
        try {
            List<Columna> columnas = obtenerColumnas();
            Tabla tabla = new Tabla(tfNombreTabla.getText(), columnas);

            dbConnection.ejecutarDDL(tabla.generarDDL());

            JOptionPane.showMessageDialog(this,
                    "Tabla creada correctamente!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear tabla:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Columna> obtenerColumnas() {
        List<Columna> lista = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            lista.add(new Columna(
                    tableModel.getValueAt(i, 0).toString(),
                    tableModel.getValueAt(i, 1).toString(),
                    (Boolean) tableModel.getValueAt(i, 2),
                    (Boolean) tableModel.getValueAt(i, 3),
                    (Boolean) tableModel.getValueAt(i, 4)
            ));
        }

        return lista;
    }
}
