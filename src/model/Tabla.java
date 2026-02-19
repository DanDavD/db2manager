package model;
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

        for (Columna col : columnas) {
            ddl.append("  ").append(col.getNombre())
               .append(" ").append(col.getTipo());
            if (col.isNotNull()) ddl.append(" NOT NULL");
            if (col.isUnique()) ddl.append(" UNIQUE");
            ddl.append(",\n");
        }

        List<String> pks = columnas.stream().filter(Columna::isPrimaryKey).map(Columna::getNombre).toList();

        if (!pks.isEmpty()) {
            ddl.append("  PRIMARY KEY(").append(String.join(", ", pks)).append(")\n");
        } else {
            ddl.setLength(ddl.length() - 2); // quitar la última coma
        }

        ddl.append(");");
        return ddl.toString();
    }
}
