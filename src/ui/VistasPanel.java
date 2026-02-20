package ui;

import model.Vista;
import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VistasPanel extends JPanel {

    private JTable tableVistas;
    private DefaultTableModel tableModel;
    private List<Vista> listaVistas;
    private DBConnection dbConnection;

    public VistasPanel(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.listaVistas = new ArrayList<>();
        initComponents();
        cargarVistas();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Tabla de vistas (solo nombre)
        String[] columnas = {"Nombre"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo lectura
            }
        };
        tableVistas = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(tableVistas);
        add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel();

        JButton btnCrear = new JButton("Crear Vista");
        JButton btnVerDDL = new JButton("Ver DDL");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        buttonPanel.add(btnCrear);
        buttonPanel.add(btnVerDDL);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);

        add(buttonPanel, BorderLayout.SOUTH);

        // Eventos
        btnCrear.addActionListener(e -> abrirCrearVista());
        btnVerDDL.addActionListener(e -> verDDL());
        btnModificar.addActionListener(e -> modificarVista());
        btnEliminar.addActionListener(e -> eliminarVista());
    }

    // Carga las vistas desde la base de datos
    private void cargarVistas() {
        try {
            listaVistas = dbConnection.listarVistas(); // Método que retorna List<Vista>
            tableModel.setRowCount(0); // limpiar tabla

            for (Vista v : listaVistas) {
                tableModel.addRow(new Object[]{ v.getNombre() });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar vistas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vista getVistaSeleccionada() {
        int fila = tableVistas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una vista",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return listaVistas.get(fila);
    }

    private void abrirCrearVista() {
        CrearVistaView crearView = new CrearVistaView(dbConnection);
        crearView.setVisible(true);
        crearView.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                cargarVistas(); // recargar al cerrar
            }
        });
    }

    private void verDDL() {
        Vista vista = getVistaSeleccionada();
        if (vista != null) {
            JOptionPane.showMessageDialog(this,
                    vista.generarDDL(),
                    "DDL de " + vista.getNombre(),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void modificarVista() {
        Vista vista = getVistaSeleccionada();
        if (vista != null) {
            CrearVistaView modificarView = new CrearVistaView(dbConnection);
            modificarView.setNombreVista(vista.getNombre());
            modificarView.setSelectSQL(vista.getSqlSelect());
            modificarView.setVisible(true);
            modificarView.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    cargarVistas();
                }
            });
        }
    }

    private void eliminarVista() {
        Vista vista = getVistaSeleccionada();
        if (vista != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la vista " + vista.getNombre() + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    dbConnection.ejecutarDDL("DROP VIEW " + vista.getNombre());
                    cargarVistas();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Error al eliminar la vista:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}