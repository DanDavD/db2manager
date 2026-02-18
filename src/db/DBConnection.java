package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private Connection connection;

    public void connect(String host, int port, String db, String user, String pass) throws SQLException {
        String url = "jdbc:db2://" + host + ":" + port + "/" + db;
        connection = DriverManager.getConnection(url, user, pass);
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}

