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
        setSize(800, 600); // Un poco más grande para que quepan bien los botones
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // 1. Configuración de Lista de Conexiones
        listModel = new DefaultListModel<>();
        for (String name : connectionManager.listConnectionNames()) {
            listModel.addElement(name);
        }
        connectionList = new JList<>(listModel);
        JScrollPane scrollConexiones = new JScrollPane(connectionList);
        scrollConexiones.setPreferredSize(new Dimension(200, 0));

        // 2. Panel Central con Editor SQL
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel panelSQL = new JPanel(new BorderLayout());
        JTextArea txtQuery = new JTextArea("SELECT * FROM SYSCAT.TABLES WHERE TABSCHEMA NOT LIKE 'SYS%'");
        txtQuery.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JButton btnEjecutarSQL = new JButton("Ejecutar Query (SELECT)");
        JTable tablaResultados = new JTable();
        JScrollPane scrollResultados = new JScrollPane(tablaResultados);

        JPanel panelInferiorSQL = new JPanel(new BorderLayout());
        panelInferiorSQL.setPreferredSize(new Dimension(0, 250));
        panelInferiorSQL.add(btnEjecutarSQL, BorderLayout.NORTH);
        panelInferiorSQL.add(scrollResultados, BorderLayout.CENTER);

        panelSQL.add(new JScrollPane(txtQuery), BorderLayout.CENTER);
        panelSQL.add(panelInferiorSQL, BorderLayout.SOUTH);
        tabbedPane.addTab("Editor SQL", panelSQL);

        // 3. Layout Principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollConexiones, tabbedPane);
        add(splitPane, BorderLayout.CENTER);

        // 4. Panel Superior de Botones (Ordenado)
        JPanel panelBotones = new JPanel();

        JButton btnCrearTabla = new JButton("Nueva Tabla");
        btnCrearTabla.addActionListener(e -> abrirCrearTabla());

        JButton btnVistas = new JButton("Vistas");
        btnVistas.addActionListener(e -> abrirVistasPanel());

        JButton btnExplorar = new JButton("Explorar Objetos");
        btnExplorar.addActionListener(e -> abrirExploradorMaestro());

        JButton btnRefrescar = new JButton("Actualizar Lista");
        btnRefrescar.addActionListener(e -> refrescarConexiones());

        panelBotones.add(btnCrearTabla);
        panelBotones.add(btnVistas);
        panelBotones.add(btnExplorar);
        panelBotones.add(btnRefrescar);

        add(panelBotones, BorderLayout.NORTH);

        // Acción del Botón SQL
        btnEjecutarSQL.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Ejecutando: " + txtQuery.getText());
        });
    }

    // --- MÉTODOS DE APOYO (SIGUIENDO TU ESTÁNDAR) ---

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
            new CrearTablaView(db).setVisible(true);
        } else {
            mostrarAvisoConexion();
        }
    }

    private void abrirVistasPanel() {
        String selectedName = connectionList.getSelectedValue();
        if (selectedName != null) {
            DBConnection db = connectionManager.getConnection(selectedName);
            JFrame frame = new JFrame("Vistas - " + selectedName);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(this);
            frame.add(new VistasPanel(db)); // Asumiendo que VistasPanel es JPanel
            frame.setVisible(true);
        } else {
            mostrarAvisoConexion();
        }
    }

    private void abrirExploradorMaestro() {
        String selectedName = connectionList.getSelectedValue();
        if (selectedName != null) {
            DBConnection db = connectionManager.getConnection(selectedName);
            // Si ExploradorObjetosView hereda de JFrame lo llamas así:
            new ExploradorObjetosView(db).setVisible(true);
        } else {
            mostrarAvisoConexion();
        }
    }

    private void mostrarAvisoConexion() {
        JOptionPane.showMessageDialog(this,
                "Seleccione una conexión primero de la lista de la izquierda.",
                "Atención",
                JOptionPane.WARNING_MESSAGE);
    }
}