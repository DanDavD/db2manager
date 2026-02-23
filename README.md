# db2manager

DB2 System Administration Tool
Asignatura: Teoría de Bases de Datos II
SGBD: IBM DB2 LUW (Linux, Unix, Windows)

Esta herramienta es un cliente JDBC de escritorio desarrollado para la gestión de objetos y auditoría de esquemas en entornos IBM DB2. El núcleo del sistema se basa en la interacción directa con las vistas de catálogo del sistema (SYSCAT), cumpliendo estrictamente con el requerimiento de no utilizar information_schema ni frameworks de persistencia (ORM), garantizando una manipulación de metadatos de bajo nivel.

Arquitectura Técnica
Lenguaje: Java 17.

Interfaz Gráfica: Java Swing (Arquitectura modular basada en JFrame y JPanel).

Conectividad: JDBC nativo mediante el driver db2jcc4.

Metodología: Ingeniería inversa mediante consultas directas al catálogo de sistema de IBM.

Análisis de Funcionalidades Implementadas

1. Gestión de Conexiones y Autenticación
   Autenticación Dinámica: Capacidad de inicio de sesión para cualquier usuario con credenciales válidas en la instancia de DB2.

Gestión Multi-sesión: Implementación de la clase ConnectionManager para el almacenamiento y recuperación de múltiples perfiles de conexión activos simultáneamente mediante un mapeo de identificadores únicos.

2. Administración de Objetos (Vistas de Sistema)
   El sistema extrae información de los siguientes catálogos:

Tablas: SYSCAT.TABLES (Filtro TYPE = 'T').

Vistas: SYSCAT.VIEWS y SYSCAT.TABLES (Filtro TYPE = 'V').

Índices: SYSCAT.INDEXES.

Triggers: SYSCAT.TRIGGERS.

Procedimientos y Funciones: SYSCAT.PROCEDURES y SYSCAT.FUNCTIONS.

Secuencias: SYSCAT.SEQUENCES.

Tablespaces: SYSCAT.TABLESPACES.

Usuarios: SYSCAT.DBAUTH.

3. Ingeniería Inversa y Operaciones DDL
   Generación de DDL de Tablas: Algoritmo de reconstrucción manual que procesa tipos de datos, longitudes, nulidad y restricciones de unicidad.

Soporte de Llaves Primarias: Identificación de Constraints de tabla mediante SYSCAT.KEYCOLUSE para soportar llaves primarias compuestas.

Generación de DDL de Vistas: Recuperación íntegra de la sentencia SELECT original almacenada en el catálogo.

Creación Visual: Interfaces CrearTablaView y CrearVistaView con lógica de validación de integridad referencial básica.

4. Motor de Ejecución SQL
   Procesamiento de Resultados: Visualización dinámica de sentencias SELECT mediante el uso de ResultSetMetaData para la generación de modelos de tabla en tiempo de ejecución.

Ejecución de Scripts: Soporte para comandos DDL (CREATE, DROP) y DML (INSERT, UPDATE) con reporte de estado y filas afectadas.

Limitaciones Técnicas y Justificaciones
Siguiendo los requerimientos de documentación de diferencias en el SGBD, se detallan los siguientes puntos:

Paquetes (Packages): En DB2, los paquetes están vinculados a SQL estático y planes de acceso. Su administración suele requerir utilidades de Bind a nivel de servidor. Dada la naturaleza de este cliente basado en SQL dinámico, el soporte para paquetes se limita a su avistamiento en el catálogo.

Tablespaces: La reconstrucción del DDL completo de un Tablespace es parcial. Debido a que DB2 utiliza contenedores físicos (SMS/DMS), la ubicación de los archivos no es siempre accesible vía JDBC estándar. Se optó por mostrar las propiedades técnicas de gestión de almacenamiento (Page Size, Extent Size) en lugar de un CREATE TABLESPACE ejecutable.

Modificación de Objetos: El sistema permite la edición mediante la exportación del DDL actual al Editor SQL. No se implementaron asistentes visuales de ALTER TABLE debido a que DB2 impone restricciones estrictas de REORG sobre las tablas tras ciertos cambios estructurales.

Privilegios de Usuario: La auditoría se centró en privilegios de base de datos (DBAUTH). Los permisos específicos sobre tablas (TABAUTH) fueron excluidos del alcance inicial para priorizar la administración de la estructura de objetos.

Instrucciones de Configuración
Clonar el repositorio.

Incluir el archivo db2jcc4.jar en las librerías del proyecto.

Compilar y ejecutar la clase ui.LoginView para iniciar la aplicación.
