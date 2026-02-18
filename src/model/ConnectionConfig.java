package model;

public class ConnectionConfig {

    private String host;
    private int port;
    private String database;
    private String user;

    public ConnectionConfig(String host, int port, String database, String user) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUser() { return user; }
}
