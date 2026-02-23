package db;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import java.sql.*;

import model.Columna;
import model.Tabla;
import model.Vista;


public class DBConnection {

    private Connection connection;

    /**
     * Conecta a la base de datos DB2 usando TCP/IP.
     *
     * @param host     host donde está DB2 (ej. localhost)
     * @param port     puerto del servicio TCP/IP (ej. 25000)
     * @param database nombre de la base de datos
     * @param user     usuario DB2
     * @param password contraseña del usuario
     * @throws ClassNotFoundException si el driver JDBC no se encuentra
     * @throws SQLException           si hay error en la conexión
     */
    public void connect(String host, int port, String database, String user, String password) 
        throws ClassNotFoundException, SQLException {
    Class.forName("com.ibm.db2.jcc.DB2Driver");
    String url = "jdbc:db2://" + host + ":" + port + "/" + database;
    connection = DriverManager.getConnection(url, user, password);
}

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public void ejecutarDDL(String ddl) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(ddl);
            System.out.println("DDL ejecutado correctamente!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Vista> listarVistas() throws SQLException {
        List<Vista> vistas = new ArrayList<>();

        String sql = "SELECT VIEWNAME, TEXT FROM SYSCAT.VIEWS"; // TEXT contiene el SELECT

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombre = rs.getString("VIEWNAME");
                String selectSQL = rs.getString("TEXT");
                vistas.add(new Vista(nombre, selectSQL));
            }
        }

        return vistas;
    }

public Tabla obtenerTablaDesdeMetadata(String nombreTabla) throws SQLException {
    List<Columna> columnas = new ArrayList<>();
    
    // Query para obtener columnas y detectar si son PK, Not Null, toda la info basicamente
    String sql = "SELECT c.COLNAME, c.TYPENAME, c.LENGTH, c.NULLS, " +
                 "(SELECT count(*) FROM SYSCAT.KEYCOLUSE k " +
                 " WHERE k.TABNAME = c.TABNAME AND k.COLNAME = c.COLNAME) as IS_PK " +
                 "FROM SYSCAT.COLUMNS c " +
                 "WHERE c.TABNAME = '" + nombreTabla.toUpperCase() + "' " +
                 "ORDER BY c.COLNO";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            String nombre = rs.getString("COLNAME");
            String tipo = rs.getString("TYPENAME");
            int longitud = rs.getInt("LENGTH");
            boolean isPk = rs.getInt("IS_PK") > 0;
            boolean isNotNull = rs.getString("NULLS").equals("N");
            
            // Ajusta el tipo para que incluya la longitud 
            String tipoCompleto = tipo;
            if (tipo.contains("CHAR") || tipo.contains("GRAPHIC")) {
                tipoCompleto += "(" + longitud + ")";
            }

            // usa el modelo columna 
            columnas.add(new Columna(nombre, tipoCompleto, isPk, isNotNull, false));
        }
    }
    
    if (columnas.isEmpty()) return null;
    return new Tabla(nombreTabla, columnas);
}

public List<String> listarObjetos(String tipo) throws SQLException {
    List<String> objetos = new ArrayList<>();
    String sql = "";
    
    // El switch debe coincidir EXACTAMENTE con el JComboBox del Explorador
    switch (tipo) {
        case "TABLAS":
        sql = "SELECT TABNAME FROM SYSCAT.TABLES WHERE TYPE = 'T' AND TABSCHEMA NOT LIKE 'SYS%'";
        break;
    case "VISTAS":
        sql = "SELECT TABNAME FROM SYSCAT.TABLES WHERE TYPE = 'V' AND TABSCHEMA NOT LIKE 'SYS%'";
        break;
    case "PROCEDIMIENTOS":
        sql = "SELECT PROCNAME FROM SYSCAT.PROCEDURES WHERE PROCSCHEMA NOT LIKE 'SYS%'";
        break;
    case "INDICES":
        sql = "SELECT INDNAME FROM SYSCAT.INDEXES WHERE INDSCHEMA NOT LIKE 'SYS%'";
        break;
    case "TRIGGERS": 
        sql = "SELECT TRIGNAME FROM SYSCAT.TRIGGERS WHERE TRIGSCHEMA NOT LIKE 'SYS%'";
        break;
    case "SECUENCIAS": 
        sql = "SELECT SEQNAME FROM SYSCAT.SEQUENCES WHERE SEQSCHEMA NOT LIKE 'SYS%'";
        break;
    case "TABLESPACES": 
        sql = "SELECT TBSPACE FROM SYSCAT.TABLESPACES";
        break;
    case "USUARIOS": 
        sql = "SELECT DISTINCT GRANTEE FROM SYSCAT.DBAUTH";
        break;
    case "FUNCIONES": 
        sql = "SELECT FUNCNAME FROM SYSCAT.FUNCTIONS WHERE FUNCSCHEMA NOT LIKE 'SYS%'";
        break;
}

    if (sql.isEmpty()) return objetos;

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            objetos.add(rs.getString(1).trim());
        }
    }
    return objetos;
}

public Object ejecutarSQL(String sql) throws SQLException {
    // limpiar sql para ver su inicial
    String query = sql.trim().toUpperCase();
    Statement stmt = connection.createStatement();

    if (query.startsWith("SELECT")) {
        //devolver el defauttable
        ResultSet rs = stmt.executeQuery(sql);
        return construirModeloTabla(rs);
    } else {
        //comando create, insert, etc
        int filasAfectadas = stmt.executeUpdate(sql);
        return filasAfectadas; // dovolver num filas o exito
    }
}

// Método auxiliar para convertir el ResultSet en algo que el JTable entienda
private DefaultTableModel construirModeloTabla(ResultSet rs) throws SQLException {
    DefaultTableModel model = new DefaultTableModel();
    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();

    for (int i = 1; i <= columnCount; i++) {
        model.addColumn(metaData.getColumnName(i));
    }

    while (rs.next()) {
        Object[] row = new Object[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            row[i - 1] = rs.getObject(i);
        }
        model.addRow(row);
    }
    return model;
}

public String obtenerDDLIndice(String nombreIndice) throws SQLException {
    String sql = "SELECT TABNAME, COLNAMES, UNIQUERULE " +
                 "FROM SYSCAT.INDEXES " +
                 "WHERE INDNAME = '" + nombreIndice.toUpperCase() + "'";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        if (rs.next()) {
            String tabla = rs.getString("TABNAME");
            String columnas = rs.getString("COLNAMES"); //col1+col2
            String unico = rs.getString("UNIQUERULE").equals("U") ? "UNIQUE " : "";
            columnas = columnas.replace("+", ", ").substring(2); 

            return "CREATE " + unico + "INDEX " + nombreIndice + 
                   "\nON " + tabla + " (" + columnas + ");";
        }
    }
    return "-- No se pudo generar el DDL del índice.";
}

public String obtenerDDLVista(String nombreVista) throws SQLException {
    
    String sql = "SELECT TEXT FROM SYSCAT.VIEWS " +
                 "WHERE VIEWNAME = '" + nombreVista.toUpperCase() + "'";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        if (rs.next()) {
            String selectOriginal = rs.getString("TEXT");
           
            return selectOriginal.trim() + ";";
        }
    }
    return "-- No se encontró el código de la vista.";
}


}
