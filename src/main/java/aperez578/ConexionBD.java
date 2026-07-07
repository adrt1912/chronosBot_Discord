package aperez578;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class ConexionBD {

    private static ConexionBD conexionBD;
    private static final String URL = "jdbc:sqlite:chronos.db";
    private static final Logger logger = LoggerFactory.getLogger(ConexionBD.class);

    private ConexionBD() {}

    public static ConexionBD getConexionBD() {
        if (conexionBD == null) conexionBD = new ConexionBD();
        return conexionBD;
    }

    public Connection obtenerConexion() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement stmt = c.createStatement()) {
            // Activamos las claves foráneas obligatorias
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return c;
    }

    public void crearTablasSiNoExisten() {
        String sqlEventos = "CREATE TABLE IF NOT EXISTS Evento (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL, " +
                "tiempo Long NOT NULL, " +
                "creador_id TEXT NOT NULL, " + // Almacena el ID de Discord del usuario
                "canal_id TEXT NOT NULL," +     // Almacena el ID del canal donde se avisará
                "rol_id TEXT NOT NULL," +
                "tipoBoton Integer NOT NULL," +
                "opciones TEXT" +
                ");";

        String sqlRecordatorio = "CREATE TABLE IF NOT EXISTS Recordatorios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id TEXT NOT NULL, " +
                "canal_id TEXT NOT NULL, " +
                "mensaje TEXT NOT NULL, " +
                "tiempo_ejecucion LONG NOT NULL" +
                ");";

        // Añade esto dentro de crearTablasSiNoExisten() en ConexionBD.java:
        String sqlConfig = "CREATE TABLE IF NOT EXISTS Configuracion (" +
                "guild_id TEXT PRIMARY KEY, " +
                "canal_alertas_id TEXT NOT NULL" +
                ");";

        String sqlApuntado = "Create TABLE if Not Exists Asistencia(" +
                "tarea_id Integer," +
                "usuario_id Text," +
                "voto TEXT," +
                "PRIMARY KEY (tarea_id, usuario_id)," +
                "FOREIGN key (tarea_id) references Evento(id) ON DELETE CASCADE" +
                ")";

        String sqlEconomia = "CREATE TABLE IF NOT EXISTS Economia (" +
                "usuario_id TEXT PRIMARY KEY, " +
                "monedas INTEGER DEFAULT 0, " +
                "ultimo_trabajo LONG DEFAULT 0" +
                ");";

        String sqlAdvertencias = "CREATE TABLE IF NOT EXISTS advertencias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id TEXT NOT NULL," +
                "guild_id TEXT NOT NULL," +
                "razon TEXT NOT NULL," +
                "mod_id TEXT NOT NULL," +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String sqlNiveles = "CREATE TABLE IF NOT EXISTS niveles (" +
                "user_id TEXT NOT NULL," +
                "guild_id TEXT NOT NULL," +
                "xp INTEGER DEFAULT 0," +
                "nivel INTEGER DEFAULT 1," +
                "PRIMARY KEY (user_id, guild_id)" +
                ");";

        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sqlEventos);
             PreparedStatement ps1 =c.prepareStatement(sqlApuntado);
             PreparedStatement ps2 = c.prepareStatement(sqlConfig);
             PreparedStatement ps3 = c.prepareStatement(sqlRecordatorio);
             PreparedStatement ps4 = c.prepareStatement(sqlEconomia);
             PreparedStatement ps5 =c.prepareStatement(sqlAdvertencias);
             PreparedStatement ps6 = c.prepareStatement(sqlNiveles)
        ) {
            ps.execute();
            ps1.execute();
            ps2.execute();
            ps3.execute();
            ps4.execute();
            ps5.execute();
            ps6.execute();
        } catch (Exception e) {
            logger.info("Error al crear alguna tabla: {}", e.getMessage());
        }
    }
}