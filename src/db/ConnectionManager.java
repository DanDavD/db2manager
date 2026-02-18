package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Administra múltiples conexiones a bases de datos DB2.
 */
public class ConnectionManager {

    // Mapa de conexiones con un nombre identificador
    private Map<String, DBConnection> connections;

    public ConnectionManager() {
        connections = new HashMap<>();
    }

    /**
     * Agrega y conecta una nueva conexión.
     *
     * @param name     Identificador único de la conexión
     * @param host     Host DB2
     * @param port     Puerto
     * @param database Nombre de la base de datos
     * @param user     Usuario
     * @param password Contraseña
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void addConnection(String name, String host, int port, String database, String user, String password)
            throws ClassNotFoundException, SQLException {

        if (connections.containsKey(name)) {
            throw new IllegalArgumentException("Ya existe una conexión con ese nombre.");
        }

        DBConnection dbConn = new DBConnection();
        dbConn.connect(host, port, database, user, password);
        connections.put(name, dbConn);
    }

    /**
     * Obtiene una conexión activa por nombre
     */
    public DBConnection getConnection(String name) {
        return connections.get(name);
    }

    /**
     * Cierra y elimina una conexión
     */
    public void removeConnection(String name) throws SQLException {
        DBConnection dbConn = connections.get(name);
        if (dbConn != null) {
            dbConn.disconnect();
            connections.remove(name);
        }
    }

    /**
     * Cierra todas las conexiones
     */
    public void closeAll() throws SQLException {
        for (DBConnection dbConn : connections.values()) {
            dbConn.disconnect();
        }
        connections.clear();
    }

    /**
     * Lista los nombres de todas las conexiones activas
     */
    public String[] listConnectionNames() {
        return connections.keySet().toArray(new String[0]);
    }
}

