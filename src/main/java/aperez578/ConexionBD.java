package aperez578;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConexionBD {

    private static ConexionBD conexionBD;
    private static final String URL = "jdbc:sqlite:chronos.db";
    private static final Logger logger = LoggerFactory.getLogger(ConexionBD.class);

    private ConexionBD() {}

    public static ConexionBD getConexionBD() {
        if (conexionBD == null) conexionBD = new ConexionBD();
        return conexionBD;
    }

    private DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Connection obtenerConexion() throws SQLException {
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

        // 🌟 CORREGIDO: Mismos nombres de columna que usan el INSERT, SELECT y DELETE
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

        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sqlEventos);
             PreparedStatement ps1 =c.prepareStatement(sqlApuntado);
             PreparedStatement ps2 = c.prepareStatement(sqlConfig);
             PreparedStatement ps3 = c.prepareStatement(sqlRecordatorio)) {
            ps.execute();
            ps1.execute();
            ps2.execute();
            ps3.execute();
        } catch (Exception e) {
            logger.info("Error al crear alguna tabla: {}", e.getMessage());
        }
    }

    public boolean crearTarea(String titulo, long timestapmp, String idAutor, String idCanal, String mencionExtra,int tipoBoton,String opciones) {
        String op = "insert or replace into Evento(titulo,tiempo,creador_id,canal_id,rol_id,tipoBoton,opciones) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = obtenerConexion();
             PreparedStatement psGuardar = c.prepareStatement(op)
        ) {
            psGuardar.setString(1, titulo);
            psGuardar.setLong(2, timestapmp);
            psGuardar.setString(3, idAutor);
            psGuardar.setString(4, idCanal);
            psGuardar.setString(5, mencionExtra);
            psGuardar.setInt(6,tipoBoton);
            psGuardar.setString(7,opciones);
            int numA = psGuardar.executeUpdate();
            return numA == 1;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public List<Tarea> listarTareas(String idUser) {
        List<Tarea> tareaList = new ArrayList<>();
        String op = "select * from Evento where creador_id=?";
        try (Connection c = obtenerConexion();
             PreparedStatement psGuardar = c.prepareStatement(op)
        ) {
            psGuardar.setString(1, idUser);
            ResultSet rs = psGuardar.executeQuery();

            // 1. Añadimos el while para recorrer los resultados de forma segura
            while (rs.next()) {
                // 2. Usamos los nombres EXACTOS de las columnas de tu CREATE TABLE
                String titulo = rs.getString("titulo");
                long tiempo = rs.getLong("tiempo");
                String creadorId = rs.getString("creador_id"); // Antes tenías "idAutor"
                String canalId = rs.getString("canal_id");     // Antes tenías "idCanal"
                String rolId = rs.getString("rol_id"); // 1. Sacamos el rol de la BD
                int tipoBoton=rs.getInt("tipoBoton");
                String opciones=rs.getString("opciones");

                // 3. Creamos el objeto Tarea con los datos reales traducidos
                tareaList.add(new Tarea(titulo, tiempo, creadorId, canalId, rs.getInt("id"), rolId,tipoBoton,opciones));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public boolean borrarTarea(int idTarea) {

        String op = "DELETE from Evento where id=?";
        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            int num = ps.executeUpdate();
            return num > 0;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public List<Tarea> tareasAvisar(long tiempoInicio, long tiempoFin) {

        String op = "SELECT * FROM Evento WHERE tiempo BETWEEN ? AND ?";
        List<Tarea> tareaList = new ArrayList<>();
        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(op)) {
            ps.setLong(1, tiempoInicio);
            ps.setLong(2, tiempoFin);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String titulo = rs.getString("titulo");
                long tiempo = rs.getLong("tiempo");
                String creadorId = rs.getString("creador_id"); // Antes tenías "idAutor"
                String canalId = rs.getString("canal_id");     // Antes tenías "idCanal"
                String rolId = rs.getString("rol_id"); // 1. Sacamos el rol de la BD
                int tipoBoton=rs.getInt("tipoBoton");
                String opciones=rs.getString("opciones");
                // 3. Creamos el objeto Tarea con los datos reales traducidos
                tareaList.add(new Tarea(titulo, tiempo, creadorId, canalId, rs.getInt("id"), rolId,tipoBoton,opciones));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public List<Tarea> listarTareasServer(String canalID) {
        List<Tarea> tareaList = new ArrayList<>();
        String op = "SELECT * FROM Evento WHERE canal_id = ? ORDER BY tiempo ASC";
        try (Connection c = obtenerConexion();
             PreparedStatement psGuardar = c.prepareStatement(op)
        ) {
            psGuardar.setString(1, canalID);
            ResultSet rs = psGuardar.executeQuery();

            // 1. Añadimos el while para recorrer los resultados de forma segura
            while (rs.next()) {
                // 2. Usamos los nombres EXACTOS de las columnas de tu CREATE TABLE
                String titulo = rs.getString("titulo");
                long tiempo = rs.getLong("tiempo");
                String creadorId = rs.getString("creador_id"); // Antes tenías "idAutor"
                String canalId = rs.getString("canal_id");     // Antes tenías "idCanal"
                String rolId = rs.getString("rol_id"); // 1. Sacamos el rol de la BD
                int tipoBoton= rs.getInt("tipoBoton");
                String opciones=rs.getString("opciones");
                // 3. Creamos el objeto Tarea con los datos reales traducidos
                tareaList.add(new Tarea(titulo, tiempo, creadorId, canalId, rs.getInt("id"), rolId,tipoBoton,opciones));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public boolean actualizarTitulo(int id, String titulo) {

        String op = "UPDATE Evento SET titulo = ? WHERE id = ?";
        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(op)) {
            ps.setString(1, titulo); // El primer '?' ahora es el Texto
            ps.setInt(2, id);        // El segundo '?' es el ID
           return ps.executeUpdate()==1;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public boolean actualizarTiempo(int id, long nuevoTimestamp) {
        String sql = "UPDATE Evento SET tiempo = ? WHERE id = ?";
        try (Connection c = obtenerConexion();
             PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setLong(1, nuevoTimestamp);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public void apuntarseTarea(int eventoid, String userId, String voto){

        String op = "INSERT OR REPLACE INTO Asistencia(tarea_id, usuario_id, voto) VALUES (?, ?, ?)";
        try (Connection c=obtenerConexion();
        PreparedStatement ps=c.prepareStatement(op)){

            ps.setInt(1,eventoid);
            ps.setString(2,userId);
            ps.setString(3, voto);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }


    public void desApuntareseEvento(int eventoid,String userId){

        String op="Delete from Asistencia where tarea_id=? and usuario_id=?";

        try (Connection c=obtenerConexion();
             PreparedStatement ps=c.prepareStatement(op)){

            ps.setInt(1,eventoid);
            ps.setString(2,userId);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
    // 1. Busca una tarea específica por su ID para poder reconstruir su tarjeta
    public Tarea obtenerTareaPorId(int idTarea) {
        String op = "SELECT * FROM Evento WHERE id = ?";
        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Tarea(
                        rs.getString("titulo"),
                        rs.getLong("tiempo"),
                        rs.getString("creador_id"),
                        rs.getString("canal_id"),
                        rs.getInt("id"),
                        rs.getString("rol_id"),
                        rs.getInt("tipoBoton"),
                        rs.getString("opciones")
                );
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    // 2. Devuelve la lista de IDs de Discord de los usuarios apuntados al evento
    public List<String> obtenerAsistentes(int idTarea) {
        List<String> usuarios = new ArrayList<>();
        String op = "SELECT usuario_id FROM Asistencia WHERE tarea_id = ?";
        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(rs.getString("usuario_id"));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return usuarios;
    }

    // 3. Devuelve todos los votos registrados para una encuesta (0, 1, 2...)
    public List<String> obtenerTodosLosVotos(int idTarea) {
        List<String> votos = new ArrayList<>();
        String op = "SELECT voto FROM Asistencia WHERE tarea_id = ?";
        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) votos.add(rs.getString("voto"));

        } catch (Exception e) {logger.info(e.getMessage());}
        return votos;
    }

    public void guardarCanalAlertas(String guildId, String canalId) {
        String op = "INSERT OR REPLACE INTO Configuracion(guild_id, canal_alertas_id) VALUES (?, ?)";
        try (Connection c = obtenerConexion(); PreparedStatement ps = c.prepareStatement(op)) {
            ps.setString(1, guildId);
            ps.setString(2, canalId);
            ps.executeUpdate();
        } catch (Exception e) { logger.info(e.getMessage()); }
    }

    public String obtenerCanalAlertas(String guildId) {
        String op = "SELECT canal_alertas_id FROM Configuracion WHERE guild_id = ?";
        try (Connection c = obtenerConexion(); PreparedStatement ps = c.prepareStatement(op)) {
            ps.setString(1, guildId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("canal_alertas_id");
        } catch (Exception e) { logger.info(e.getMessage()); }
        return null; // Si no está configurado, devolverá null
    }
    public List<Tarea> listarTareasAsistidas(String userID) {
        List<Tarea> tareaList = new ArrayList<>();
        String sql = "SELECT e.* FROM Evento e INNER JOIN Asistencia a ON e.id = a.tarea_id WHERE a.usuario_id = ?";

        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String titulo = rs.getString("titulo");
                long tiempo = rs.getLong("tiempo");
                String creadorId = rs.getString("creador_id");
                String canalId = rs.getString("canal_id");
                String rolId = rs.getString("rol_id");
                int tipoBoton = rs.getInt("tipoBoton");
                String opciones = rs.getString("opciones");

                tareaList.add(new Tarea(titulo, tiempo, creadorId, canalId, rs.getInt("id"), rolId, tipoBoton, opciones));
            }
        } catch (Exception e) {
            logger.info("Error al listar tareas asistidas: {}", e.getMessage());
        }
        return tareaList;
    }

    public boolean guardarRecordatorio(String userId, String canalId, String mensaje, long timestamp) {
        String sql = "INSERT INTO Recordatorios(usuario_id, canal_id, mensaje, tiempo_ejecucion) VALUES (?,?,?,?)";
        try (Connection c = obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, canalId);
            ps.setString(3, mensaje);
            ps.setLong(4, timestamp);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            logger.info("Error al guardar recordatorio: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminarRecordatorio(int id){

        String op="delete from Recordatorios where id=?";

        try (Connection c=obtenerConexion();
        PreparedStatement ps=c.prepareStatement(op)){
            ps.setInt(1,id);
            return ps.executeUpdate()==1;

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return false;
    }
    public List<RecordatorioObj> obtenerRecordatoriosVencidos(long tiempoAhora) {
        List<RecordatorioObj> lista = new ArrayList<>();
        String sql = "SELECT * FROM Recordatorios WHERE tiempo_ejecucion <= ?";
        try (Connection c = obtenerConexion(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, tiempoAhora);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new RecordatorioObj(
                        rs.getInt("id"),
                        rs.getString("usuario_id"),
                        rs.getString("canal_id"),
                        rs.getString("mensaje")
                ));
            }
        } catch (Exception e) {
            logger.info("Error al buscar recordatorios vencidos: " + e.getMessage());
        }
        return lista;
    }
}