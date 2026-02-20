package model;

public class Vista {
    private String nombre;
    private String sqlSelect; // El SELECT que define la vista

    public Vista(String nombre, String sqlSelect) {
        this.nombre = nombre;
        this.sqlSelect = sqlSelect;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSqlSelect() { return sqlSelect; }
    public void setSqlSelect(String sqlSelect) { this.sqlSelect = sqlSelect; }

    // Genera el DDL CREATE VIEW automáticamente
    public String generarDDL() {
        return "CREATE VIEW " + nombre + " AS \n" + sqlSelect + ";";
    }
}