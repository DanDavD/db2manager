package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
}
