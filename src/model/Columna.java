package model;

public class Columna {
    private String nombre;
    private String tipo;
    private boolean primaryKey;
    private boolean notNull;
    private boolean unique;

    public Columna(String nombre, String tipo, boolean primaryKey, boolean notNull, boolean unique) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.primaryKey = primaryKey;
        this.notNull = notNull;
        this.unique = unique;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isPrimaryKey() { return primaryKey; }
    public void setPrimaryKey(boolean primaryKey) { this.primaryKey = primaryKey; }

    public boolean isNotNull() { return notNull; }
    public void setNotNull(boolean notNull) { this.notNull = notNull; }

    public boolean isUnique() { return unique; }
    public void setUnique(boolean unique) { this.unique = unique; }
}
