# DB2 Manager

A desktop administration tool for IBM Db2 LUW, written in Java with Swing and plain JDBC. It connects to Db2, explores the database's objects, rebuilds their DDL from the system catalog and runs SQL, without an ORM and without `information_schema`: every query goes straight to the `SYSCAT` catalog views.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![IBM Db2](https://img.shields.io/badge/IBM%20Db2-LUW-052FAD)
![Swing](https://img.shields.io/badge/UI-Swing-lightgrey)
![JDBC](https://img.shields.io/badge/JDBC-db2jcc4-blue)

Coursework for Database Theory II at UNITEC.

## What it does

- **Connections:** log in with host, port (25000 by default), database, user and password. `ConnectionManager` keeps several named connections open at once.
- **Object explorer:** lists tables, views, indexes, triggers, sequences, procedures, functions, tablespaces and users from the catalog (system schemas are left out), shows the DDL of the selected object where it can be rebuilt (see below), and drops it after a confirmation.
- **DDL from the catalog (reverse engineering):**
  - tables: `CREATE TABLE` with every column's type (with its length for character types), `NOT NULL`, and the primary key, from `SYSCAT.COLUMNS` and `SYSCAT.KEYCOLUSE`;
  - views: their original `SELECT`, from `SYSCAT.VIEWS.TEXT`;
  - indexes: `CREATE [UNIQUE] INDEX` with their columns, from `SYSCAT.INDEXES`;
  - users: the `GRANT` statements for their database authorities, from `SYSCAT.DBAUTH`;
  - tablespaces: their properties (SMS or DMS, page size, extent size, overhead).
- **Visual creation:** forms to design a table or a view, preview its DDL and create it. A primary key column is locked to `NOT NULL`, as Db2 requires.
- **SQL editor:** a `SELECT` fills a result grid built from `ResultSetMetaData`; DDL and DML report success and the rows affected.

## How it is built

| Package | Classes | Role |
|---|---|---|
| `db` | `ConnectionManager`, `DBConnection` | JDBC sessions, and every catalog query and DDL builder |
| `model` | `Tabla`, `Columna`, `Vista`, `ConnectionConfig` | The objects read from the catalog; `Tabla.generarDDL()` turns a table back into SQL |
| `ui` | `LoginView`, `MainView`, `ExploradorObjetosView`, `CrearTablaView`, `CrearVistaView`, `TablasPanel`, `VistasPanel`, `DDLView` | The Swing windows and panels |
| `app` | `Main` | Entry point |

Catalog views used: `SYSCAT.TABLES`, `COLUMNS`, `KEYCOLUSE`, `VIEWS`, `INDEXES`, `TRIGGERS`, `SEQUENCES`, `PROCEDURES`, `FUNCTIONS`, `TABLESPACES` and `DBAUTH`.

## Limitations, and why

- **Procedures, functions and triggers are listed but their DDL is not rebuilt.** Db2 stores SQL PL bodies in a segmented or precompiled form, and rebuilding them without a full SQL PL parser would produce scripts that might not run.
- **No `CREATE TABLESPACE`.** A tablespace depends on physical containers (paths on the server), which the JDBC driver does not expose without instance-level authority (`SYSADM`).
- **No `CREATE USER`.** Db2 LUW has no such statement: users live in the operating system or a directory service (LDAP, Active Directory). The tool manages what the database does own: the authorities granted to each user.
- **No DDL for packages.** Packages are compiled objects produced by a bind, with no plain-text definition in the catalog.

## Getting started

You need Java 17, a reachable Db2 LUW instance and the IBM JDBC driver `db2jcc4.jar` (it ships with Db2, in `sqllib/java`, and with the IBM Data Server Driver for JDBC). The driver is not in the repository.

From the repository folder, with the driver in `lib/`:

```
javac -cp lib/db2jcc4.jar -d out src/app/*.java src/db/*.java src/model/*.java src/ui/*.java
java -cp "out;lib/db2jcc4.jar" app.Main
```

(On Linux or macOS, use `:` instead of `;` in the classpath.) The login window opens; Db2's default port is 25000.

## Project structure

```
src/
  app/     Main, the entry point
  db/      Connections, catalog queries and DDL builders
  model/   Tables, columns and views read from the catalog
  ui/      Swing windows: login, main view, object explorer, creation forms, DDL viewer
```
