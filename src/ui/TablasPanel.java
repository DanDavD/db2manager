package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import db.DBConnection;
import model.Tabla;

public class TablasPanel extends JPanel {
    private DBConnection db;
    private JList<String> tablaList;
    private DefaultListModel<String> listModel;

    public TablasPanel(DBConnection db) {
        this.db = db;
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        tablaList = new JList<>(listModel);
        add(new JScrollPane(tablaList), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnVerDDL = new JButton("Ver DDL (Ingeniería Inversa)");
        
        btnVerDDL.addActionListener(e -> generarDDLTabla());
        
        panelBotones.add(btnVerDDL);
        add(panelBotones, BorderLayout.SOUTH);

        cargarTablas();
    }

    private void cargarTablas() {
        try {
            // Usamos el método que creamos para listar objetos tipo 'T' (Table)
            List<String> tablas = db.listarObjetos("T");
            for (String t : tablas) {
                listModel.addElement(t);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar tablas: " + e.getMessage());
        }
    }

    private void generarDDLTabla() {
        String seleccion = tablaList.getSelectedValue();
        if (seleccion != null) {
            try {
                // 1. Obtenemos la metadata y reconstruimos el modelo Tabla
                Tabla tablaCargada = db.obtenerTablaDesdeMetadata(seleccion);
                
                if (tablaCargada != null) {
                    // 2. Generamos el DDL usando tu lógica de model.Tabla
                    String ddl = tablaCargada.generarDDL();
                    
                    // 3. Mostramos el resultado en una nueva ventana de visualización
                    new DDLView(seleccion, ddl).setVisible(true);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error de Metadata: " + e.getMessage());
            }
        }
    }
}
