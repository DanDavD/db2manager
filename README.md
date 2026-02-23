DB2 SYSTEM ADMINISTRATION TOOL
Asignatura: Teoría de Bases de Datos II
SGBD: IBM DB2 LUW (Linux, Unix, Windows)
Lenguaje: Java 17 + JDBC (Driver db2jcc4)
Arquitectura: Escritorio (Java Swing)

1. INTRODUCCIÓN Y PROPÓSITO
   Este proyecto consiste en el desarrollo de una herramienta administrativa de escritorio diseñada para la gestión integral de objetos de base de datos en entornos IBM DB2. El objetivo fundamental es interactuar con el motor mediante el uso exclusivo de System Tables (Tablas de Catálogo de Sistema), evitando el esquema estandarizado information_schema y cualquier framework de persistencia de alto nivel (ORM). La aplicación permite realizar auditorías de metadatos, ingeniería inversa de estructuras existentes y ejecución de scripts SQL dinámicos de forma directa y transparente.

2. ARQUITECTURA DEL SISTEMA (DESGLOSE DE CLASES)
   El proyecto está estructurado bajo un patrón de diseño desacoplado, dividiendo la lógica de comunicación, los modelos de datos y la interfaz gráfica en 14 clases estratégicas:

A. Capa de Conectividad y Persistencia (Paquete db)
ConnectionManager: Actúa como el núcleo de gestión de sesiones. Implementa un HashMap de conexiones que permite la administración simultánea de múltiples bases de datos e instancias de DB2 sin pérdida de estado.

DBConnection: Es la clase de mayor peso técnico. Contiene la lógica de comunicación JDBC y las consultas SQL nativas hacia el catálogo SYSCAT. Se encarga de extraer metadatos crudos de columnas, llaves primarias, índices, secuencias y vistas para su posterior procesamiento.

B. Capa de Modelado e Ingeniería Inversa (Paquete model)
Tabla y Columna: Representan la abstracción lógica de una entidad física. El método generarDDL() dentro de la clase Tabla es el motor de ingeniería inversa; transforma objetos Java en scripts CREATE TABLE completos y ejecutables.

Vista: Almacena la definición de consulta recuperada directamente del campo TEXT de SYSCAT.VIEWS para su reconstrucción exacta.

ConnectionConfig: Estructura de datos utilizada para encapsular los parámetros de conexión (Host, Puerto, Database, User).

C. Capa de Interfaz de Usuario (Paquete ui)
LoginView: Ventana de autenticación con validación de credenciales y carga de driver.

MainView: Consola central que integra el editor SQL dinámico, el explorador de conexiones y el visor de resultados basado en metadatos.

ExploradorObjetosView: Browser universal de objetos que permite filtrar por tipos (Tablas, Vistas, Índices, etc.) y realizar operaciones de DROP.

CrearTablaView y CrearVistaView: Formularios interactivos para el diseño visual de objetos.

TablasPanel y VistasPanel: Componentes modulares para la gestión específica de estos objetos.

DDLView: Visor especializado de código con fuente monoespaciada para la exportación de scripts.

3. COMPARATIVA DE REQUERIMIENTOS VS. IMPLEMENTACIÓN
   3.1. Gestión de Conexiones y Autenticación (Estado: 100%)
   Se permite el inicio de sesión con cualquier usuario válido del sistema.

Capacidad probada para almacenar y alternar entre múltiples conexiones a distintas instancias de DB2.

3.2. Operaciones sobre Objetos (Estado: COMPLETADO)
Creación Visual: Interfaces dedicadas para crear tablas y vistas sin necesidad de escribir código manual.

Generación de DDL: Capacidad de leer cualquier tabla o vista existente y reconstruir su SQL de creación fielmente desde la metadata.

Modificación: Se permite la edición de objetos mediante la exportación de su DDL al editor SQL para ajustes manuales y posterior re-ejecución.

3.3. Ejecución de Sentencias SQL (Estado: 100%)
El editor SQL procesa sentencias SELECT visualizando resultados en una rejilla dinámica mediante ResultSetMetaData.

Ejecución exitosa de scripts DDL (CREATE, DROP) y DML (INSERT, UPDATE) con reporte de estado y filas afectadas.

4. AUDITORÍA DE OBJETOS MEDIANTE CATÁLOGO (SYSCAT)
   La herramienta demuestra su potencia técnica mediante consultas directas a las siguientes vistas de catálogo de IBM:

SYSCAT.TABLES: Para obtener el esquema, nombre, propietario y fecha de creación de tablas y vistas.

SYSCAT.COLUMNS: Para extraer tipos de datos, longitudes, precisión y nulidad.

SYSCAT.TABCONST y SYSCAT.KEYCOLUSE: Para identificar restricciones de integridad y llaves primarias.

SYSCAT.INDEXES: Para mapear la estructura de índices y sus reglas de unicidad.

SYSCAT.SEQUENCES: Para auditar el estado y valores de los generadores.

SYSCAT.DBAUTH: Para verificar los privilegios administrativos de los usuarios.

5. DOCUMENTACIÓN DE LIMITACIONES Y JUSTIFICACIONES TÉCNICAS
   En cumplimiento con la rúbrica de evaluación, se detallan las limitaciones del proyecto basadas en la complejidad estructural de IBM DB2:

5.1. Lógica Programable (Triggers, Funciones y Procedimientos)
Limitación: Estos objetos se listan y exploran en la metadata, pero el sistema no reconstruye su DDL de creación completo.

Justificación: En DB2, el código SQL PL de estos objetos se almacena de forma segmentada o precompilada en el catálogo. Intentar una reconstrucción manual sin un parser de lenguaje procedimental avanzado podría generar scripts incompletos o sintácticamente incorrectos. Por seguridad del esquema y estabilidad del software, el sistema solo permite verificar su existencia y parámetros básicos.

5.2. Tablespaces y Almacenamiento
Limitación: Se visualizan métricas técnicas (PAGESIZE, EXTENTSIZE, TYPE), pero no se genera la sentencia CREATE TABLESPACE.

Justificación: La creación de un Tablespace en DB2 depende de "contenedores" físicos (SMS/DMS) que son rutas de directorios en el servidor. El driver JDBC, por diseño de seguridad, no siempre expone las rutas físicas del sistema de archivos del servidor a menos que se posean privilegios de instancia (SYSADM), los cuales exceden el alcance de un usuario estándar de base de datos.

5.3. Administración de Usuarios y Seguridad
Limitación: La gestión se limita a la auditoría de permisos de usuario existentes; no se permite la creación física de usuarios.

Justificación: A diferencia de otros motores, DB2 delega la gestión de usuarios al Sistema Operativo o a servicios externos (LDAP/Active Directory). La sentencia "CREATE USER" no existe como tal dentro del SQL de DB2 LUW, por lo que la herramienta se enfoca en gestionar lo que sí reside en el motor: los permisos y roles asignados (DBADMAUTH, CONNECTAUTH).

5.4. Paquetes (Packages) e Índices Complejos
Limitación: Se omitió el DDL de paquetes y detalles de compresión de índices.

Justificación: Los paquetes son objetos binarios compilados resultantes de un proceso de Bind; no poseen una representación de texto plano reconstruible en el catálogo. En cuanto a los índices, DB2 maneja opciones de particionamiento y compresión que requieren acceso a tablas de bajo nivel (SYSIBM) que no siempre están garantizadas para usuarios sin perfil de administrador de sistema.

6. CONSIDERACIONES TÉCNICAS FINALES
   Uso de System Tables: Se evitó estrictamente el uso de frameworks (Hibernate/JPA) para garantizar el uso explícito de SQL nativo contra el catálogo de IBM.

Integridad Referencial: El sistema incluye validaciones preventivas en la UI, como el bloqueo automático de la opción "Nullable" cuando una columna es definida como Primary Key, respetando las reglas de integridad de DB2.

Modularidad: El diseño permite que la lógica de generación de SQL (Modelo) sea independiente de la ejecución (Base de Datos), facilitando futuras extensiones del software.

Instrucciones de Ejecución:

Verifique la existencia del driver db2jcc4.jar en el classpath del proyecto.

Ejecute la clase ui.LoginView para iniciar el asistente de conexión.

Utilizar el puerto estándar 25000 para conexiones remotas a instancias DB2.
