package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ExploradorObjetosView extends JFrame {
    private DBConnection db;
    private JComboBox<String> cbTipos;
    private JList<String> listaObjetos;
    private DefaultListModel<String> listModel;

    public ExploradorObjetosView(DBConnection db) {
        this.db = db;
        setTitle("Administrador de Objetos DB2");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR (Selector) ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Filtro de Objetos"));
        
        // El JComboBox con los nombres que tu switch espera
        cbTipos = new JComboBox<>(new String[]{
    "TABLAS", "VISTAS", "INDICES", "TRIGGERS", 
    "PROCEDIMIENTOS", "FUNCIONES", "SECUENCIAS", 
    "TABLESPACES", "USUARIOS"
});
        cbTipos.addActionListener(e -> cargarObjetos()); // Recarga al cambiar opción
        
        panelSuperior.add(new JLabel("Tipo de Objeto:"));
        panelSuperior.add(cbTipos);

        // --- PANEL CENTRAL (Lista) ---
        listModel = new DefaultListModel<>();
        listaObjetos = new JList<>(listModel);
        listaObjetos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(listaObjetos);
        scrollLista.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // --- PANEL INFERIOR (Botones) ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnDDL = new JButton("Ver Estructura (DDL)");
        JButton btnEliminar = new JButton("Eliminar Objeto");

        btnDDL.addActionListener(e -> verEstructura());
        btnEliminar.addActionListener(e -> borrarSeleccionado());

        panelAcciones.add(btnDDL);
        panelAcciones.add(btnEliminar);

        // Agregar todo al Frame
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollLista, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);

        // Carga automática al abrir
        cargarObjetos();
    }

    private void cargarObjetos() {
        listModel.clear();
        String tipo = (String) cbTipos.getSelectedItem();
        try {
            List<String> resultados = db.listarObjetos(tipo);
            for (String item : resultados) {
                listModel.addElement(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + e.getMessage());
        }
    }

    private void verEstructura() {
    String seleccionado = listaObjetos.getSelectedValue();
    String tipo = (String) cbTipos.getSelectedItem();
    
    if (seleccionado == null) return;

    try {
        String ddl = "";
        if (tipo.equals("TABLAS")) {
            ddl = db.obtenerTablaDesdeMetadata(seleccionado).generarDDL();
        } else if (tipo.equals("VISTAS")) {
            ddl = db.obtenerDDLVista(seleccionado); 
        } else if (tipo.equals("INDICES")) {
            ddl = db.obtenerDDLIndice(seleccionado); 
        } else if (tipo.equals("TABLESPACES")) {
            ddl = db.obtenerInfoTablespace(seleccionado);
        } else if (tipo.equals("USUARIOS")) {
            ddl = db.obtenerDDLUsuario(seleccionado);
        } else {
            ddl = "-- Ingeniería inversa para " + tipo + " en desarrollo.\n-- Puedes ver su existencia en SYSCAT.";
        }
        
        new DDLView(seleccionado, ddl).setVisible(true);
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

    private void borrarSeleccionado() {
        String seleccionado = listaObjetos.getSelectedValue();
        String tipo = (String) cbTipos.getSelectedItem();
        
        if (seleccionado == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de borrar " + seleccionado + "?", "Confirmar DROP", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
              
                String sqlTipo = "";
                switch(tipo) {
                    case "TABLAS": sqlTipo = "TABLE"; break;
                    case "VISTAS": sqlTipo = "VIEW"; break;
                    case "PROCEDIMIENTOS": sqlTipo = "PROCEDURE"; break;
                    case "INDICES": sqlTipo = "INDEX"; break;
                }
                
                db.ejecutarDDL("DROP " + sqlTipo + " " + seleccionado);
                cargarObjetos(); // Refrescar lista auto
                JOptionPane.showMessageDialog(this, "Objeto eliminado correctamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar: " + e.getMessage());
            }
        }
    }
}