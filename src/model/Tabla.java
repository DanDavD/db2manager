package model;
import java.util.ArrayList;
import java.util.List;

public class Tabla {
    private String nombre;
    private List<Columna> columnas;

    public Tabla(String nombre, List<Columna> columnas) {
        this.nombre = nombre;
        this.columnas = columnas;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Columna> getColumnas() { return columnas; }
    public void setColumnas(List<Columna> columnas) { this.columnas = columnas; }

    // Genera el DDL CREATE TABLE automáticamente
    public String generarDDL() {
    StringBuilder ddl = new StringBuilder();
    ddl.append("CREATE TABLE ").append(nombre).append(" (\n");

    List<String> lineasColumnas = new ArrayList<>();
    for (Columna col : columnas) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(col.getNombre()).append(" ").append(col.getTipo());
        
        // Si agregas longitud en el modelo, úsala aquí:
        // if(col.hasLength()) sb.append("(").append(col.getLongitud()).append(")");

        if (col.isNotNull()) sb.append(" NOT NULL");
        if (col.isUnique()) sb.append(" UNIQUE");
        lineasColumnas.add(sb.toString());
    }

    // Unimos columnas con comas
    ddl.append(String.join(",\n", lineasColumnas));

    // Agregar PK si existe
    List<String> pks = columnas.stream()
            .filter(Columna::isPrimaryKey)
            .map(Columna::getNombre)
            .toList();

    if (!pks.isEmpty()) {
        ddl.append(",\n  PRIMARY KEY (").append(String.join(", ", pks)).append(")");
    }

    ddl.append("\n);");
    return ddl.toString();
}
}
