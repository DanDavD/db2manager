package ui;

import javax.swing.*;

import com.ibm.db2.jcc.am.de;

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
    // 1. Configuración del Modelo de Lista de Conexiones (Mantiene tu lógica)
    listModel = new DefaultListModel<>();
    String[] connections = connectionManager.listConnectionNames();
    for (String name : connections) {
        listModel.addElement(name);
    }
    connectionList = new JList<>(listModel);
    JScrollPane scrollConexiones = new JScrollPane(connectionList);
    scrollConexiones.setPreferredSize(new Dimension(200, 0)); // Ancho fijo para la izquierda

    // 2. Panel Central con Pestañas (Para cumplir con Ejecución SQL)
    JTabbedPane tabbedPane = new JTabbedPane();

    // Pestaña de Consola SQL
    JPanel panelSQL = new JPanel(new BorderLayout());
    JTextArea txtQuery = new JTextArea("SELECT * FROM SYSCAT.TABLES WHERE TABSCHEMA NOT LIKE 'SYS%'");
    txtQuery.setFont(new Font("Monospaced", Font.PLAIN, 12));
    JButton btnEjecutarSQL = new JButton("Ejecutar Query (SELECT)");
    
    // Tabla para resultados (Puntos de Ejecución de Sentencias)
    JTable tablaResultados = new JTable();
    JScrollPane scrollResultados = new JScrollPane(tablaResultados);

    panelSQL.add(new JScrollPane(txtQuery), BorderLayout.CENTER);
    
    // Panel sur para el botón y los resultados
    JPanel panelInferiorSQL = new JPanel(new BorderLayout());
    panelInferiorSQL.setPreferredSize(new Dimension(0, 200));
    panelInferiorSQL.add(btnEjecutarSQL, BorderLayout.NORTH);
    panelInferiorSQL.add(scrollResultados, BorderLayout.CENTER);
    
    panelSQL.add(panelInferiorSQL, BorderLayout.SOUTH);
    tabbedPane.addTab("Editor SQL / Resultados", panelSQL);

    // 3. SplitPane para organizar Izquierda (Conexiones) y Derecha (Contenido)
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollConexiones, tabbedPane);
    add(splitPane, BorderLayout.CENTER);

    // 4. Panel de botones Superior (Mantiene tus funciones originales)
    JPanel panelBotones = new JPanel();
    
    JButton btnCrearTabla = new JButton("Nueva Tabla");
    btnCrearTabla.addActionListener(e -> abrirCrearTabla());

    JButton btnVistas = new JButton("Explorar Vistas");
    btnVistas.addActionListener(e -> abrirVistasPanel());
    
    JButton btnRefrescar = new JButton("Actualizar Lista");
    btnRefrescar.addActionListener(e -> refrescarConexiones());

    panelBotones.add(btnCrearTabla);
    panelBotones.add(btnVistas);
    panelBotones.add(btnRefrescar);

    add(panelBotones, BorderLayout.NORTH);

    // ejecutar sql
    btnEjecutarSQL.addActionListener(e -> {
        //llamar metodo para ejecutar sql mas tarde
        JOptionPane.showMessageDialog(this, "Ejecutando: " + txtQuery.getText());
    });

    JButton btnExplorarTablas = new JButton("Explorar Tablas");
btnExplorarTablas.addActionListener(e -> abrirTablasPanel());
panelBotones.add(btnExplorarTablas);
}

// refrescar lista para nuevas conexiones
private void refrescarConexiones() {
    listModel.clear();
    for (String name : connectionManager.listConnectionNames()) {
        listModel.addElement(name);
    }
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

    private void abrirVistasPanel() {
    String selectedName = connectionList.getSelectedValue();
    if (selectedName != null) {
        DBConnection db = connectionManager.getConnection(selectedName);

      
        JFrame frameVistas = new JFrame("Vistas - " + selectedName);
        frameVistas.setSize(800, 500);
        frameVistas.setLocationRelativeTo(this);
        frameVistas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        VistasPanel vistasPanel = new VistasPanel(db);
        frameVistas.add(vistasPanel);
        frameVistas.setVisible(true);

    } else {
        JOptionPane.showMessageDialog(this,
                "Seleccione una conexión primero.",
                "Atención",
                JOptionPane.WARNING_MESSAGE);
    }
}

private void abrirTablasPanel() {
    String selectedName = connectionList.getSelectedValue();
    if (selectedName != null) {
        DBConnection db = connectionManager.getConnection(selectedName);
        
        JFrame frameTablas = new JFrame("Administrador de Tablas - " + selectedName);
        frameTablas.setSize(400, 600);
        frameTablas.setLocationRelativeTo(this);
        
        frameTablas.add(new TablasPanel(db));
        frameTablas.setVisible(true);
    } else {
        JOptionPane.showMessageDialog(this, "Seleccione una conexión primero.");
    }
}



}
