package ui;

import javax.swing.*;
import java.awt.*;

public class DDLView extends JFrame {
    public DDLView(String nombre, String ddl) {
        setTitle("DDL de la Tabla: " + nombre);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        JTextArea txtDDL = new JTextArea(ddl);
        txtDDL.setEditable(false);
        txtDDL.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        add(new JScrollPane(txtDDL), BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        add(btnCerrar, BorderLayout.SOUTH);
    }
}
