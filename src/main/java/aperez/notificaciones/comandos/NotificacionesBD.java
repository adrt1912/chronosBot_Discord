package aperez.notificaciones.comandos;

import aperez.ConexionBD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesBD {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionesBD.class);

    private static final String PARAM_TITULO = "titulo";
    private static final String PARAM_TIEMPO = "tiempo";
    private static final String PARAM_CREADORID = "creador_id";
    private static final String PARAM_CANALID = "canal_id";
    private static final String PARAM_ROLID = "rol_id";
    private static final String PARAM_TIPOBOTON = "tipoBoton";
    private static final String PARAM_OPCIONES = "opciones";

    private NotificacionesBD(){}

    public static boolean crearTarea(String titulo, long timestapmp, String idAutor, String idCanal, String mencionExtra, int tipoBoton, String opciones) {
        String op = "insert or replace into Evento(titulo,tiempo,creador_id,canal_id,rol_id,tipoBoton,opciones) VALUES (?,?,?,?,?,?,?)";
        // 🔌 Cada método pide su propia conexión local 'conn'
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement psGuardar = conn.prepareStatement(op)) {
            psGuardar.setString(1, titulo);
            psGuardar.setLong(2, timestapmp);
            psGuardar.setString(3, idAutor);
            psGuardar.setString(4, idCanal);
            psGuardar.setString(5, mencionExtra);
            psGuardar.setInt(6, tipoBoton);
            psGuardar.setString(7, opciones);
            return psGuardar.executeUpdate() == 1;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public static List<Tarea> listarTareas(String idUser) {
        List<Tarea> tareaList = new ArrayList<>();
        String op = "select id, titulo, tiempo, creador_id, canal_id, rol_id, tipoBoton, opciones from Evento where creador_id=?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement psGuardar = conn.prepareStatement(op)) {
            psGuardar.setString(1, idUser);
            try (ResultSet rs = psGuardar.executeQuery()) {
                while (rs.next()) {
                    tareaList.add(new Tarea(
                            rs.getString(PARAM_TITULO),
                            rs.getLong(PARAM_TIEMPO),
                            rs.getString(PARAM_CREADORID),
                            rs.getString(PARAM_CANALID),
                            rs.getInt("id"),
                            rs.getString(PARAM_ROLID),
                            rs.getInt(PARAM_TIPOBOTON),
                            rs.getString(PARAM_OPCIONES)
                    ));
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public static void borrarTarea(int idTarea) {
        String op = "DELETE from Evento where id=?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static List<Tarea> tareasAvisar(long tiempoInicio, long tiempoFin) {
        String op = "SELECT id, titulo, tiempo, creador_id, canal_id, rol_id, tipoBoton, opciones FROM Evento WHERE tiempo BETWEEN ? AND ?";
        List<Tarea> tareaList = new ArrayList<>();
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)) {
            ps.setLong(1, tiempoInicio);
            ps.setLong(2, tiempoFin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareaList.add(new Tarea(
                            rs.getString(PARAM_TITULO),
                            rs.getLong(PARAM_TIEMPO),
                            rs.getString(PARAM_CREADORID),
                            rs.getString(PARAM_CANALID),
                            rs.getInt("id"),
                            rs.getString(PARAM_ROLID),
                            rs.getInt(PARAM_TIPOBOTON),
                            rs.getString(PARAM_OPCIONES)
                    ));
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public static List<Tarea> listarTareasServer(String canalID) {
        List<Tarea> tareaList = new ArrayList<>();
        String op = "SELECT id, titulo, tiempo, creador_id, canal_id, rol_id, tipoBoton, opciones FROM Evento WHERE canal_id = ? ORDER BY tiempo ASC";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement psGuardar = conn.prepareStatement(op)) {
            psGuardar.setString(1, canalID);
            try (ResultSet rs = psGuardar.executeQuery()) {
                while (rs.next()) {
                    tareaList.add(new Tarea(
                            rs.getString(PARAM_TITULO),
                            rs.getLong(PARAM_TIEMPO),
                            rs.getString(PARAM_CREADORID),
                            rs.getString(PARAM_CANALID),
                            rs.getInt("id"),
                            rs.getString(PARAM_ROLID),
                            rs.getInt(PARAM_TIPOBOTON),
                            rs.getString(PARAM_OPCIONES)
                    ));
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public static boolean actualizarTitulo(int id, String titulo) {
        String op = "UPDATE Evento SET titulo = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)) {
            ps.setString(1, titulo);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public static boolean actualizarTiempo(int id, long nuevoTimestamp) {
        String sql = "UPDATE Evento SET tiempo = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, nuevoTimestamp);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public static void apuntarseTarea(int eventoid, String userId, String voto){
        String op = "INSERT OR REPLACE INTO Asistencia(tarea_id, usuario_id, voto) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)){
            ps.setInt(1, eventoid);
            ps.setString(2, userId);
            ps.setString(3, voto);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static void desApuntareseEvento(int eventoid, String userId){
        String op = "Delete from Asistencia where tarea_id=? and usuario_id=?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)){
            ps.setInt(1, eventoid);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static Tarea obtenerTareaPorId(int idTarea) {
        String op = "SELECT id, titulo, tiempo, creador_id, canal_id, rol_id, tipoBoton, opciones FROM Evento WHERE id = ?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Tarea(
                            rs.getString(PARAM_TITULO),
                            rs.getLong(PARAM_TIEMPO),
                            rs.getString(PARAM_CREADORID),
                            rs.getString(PARAM_CANALID),
                            rs.getInt("id"),
                            rs.getString(PARAM_ROLID),
                            rs.getInt(PARAM_TIPOBOTON),
                            rs.getString(PARAM_OPCIONES)
                    );
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public static List<String> obtenerAsistentes(int idTarea) {
        List<String> usuarios = new ArrayList<>();
        String op = "SELECT usuario_id FROM Asistencia WHERE tarea_id = ?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(rs.getString("usuario_id"));
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return usuarios;
    }

    public static List<String> obtenerTodosLosVotos(int idTarea) {
        List<String> votos = new ArrayList<>();
        String op = "SELECT voto FROM Asistencia WHERE tarea_id = ?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) votos.add(rs.getString("voto"));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return votos;
    }

    public static void guardarCanalAlertas(String guildId, String canalId) {
        String op = "INSERT OR REPLACE INTO Configurar(guild_id, canal_alertas_id) VALUES (?, ?)";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)) {
            ps.setString(1, guildId);
            ps.setString(2, canalId);
            ps.executeUpdate();
        } catch (Exception e) { logger.info(e.getMessage()); }
    }

    public static String obtenerCanalAlertas(String guildId) {
        String op = "SELECT canal_alertas_id FROM Configurar WHERE guild_id = ?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)) {
            ps.setString(1, guildId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("canal_alertas_id");
            }
        } catch (Exception e) { logger.info(e.getMessage()); }
        return null;
    }

    public static List<Tarea> listarTareasAsistidas(String userID) {
        List<Tarea> tareaList = new ArrayList<>();
        String sql = "SELECT e.* FROM Evento e INNER JOIN Asistencia a ON e.id = a.tarea_id WHERE a.usuario_id = ?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareaList.add(new Tarea(
                            rs.getString(PARAM_TITULO),
                            rs.getLong(PARAM_TIEMPO),
                            rs.getString(PARAM_CREADORID),
                            rs.getString(PARAM_CANALID),
                            rs.getInt("id"),
                            rs.getString(PARAM_ROLID),
                            rs.getInt(PARAM_TIPOBOTON),
                            rs.getString(PARAM_OPCIONES)
                    ));
                }
            }
        } catch (Exception e) {
            logger.info("Error al listar tareas asistidas: {}", e.getMessage());
        }
        return tareaList;
    }

    public static boolean guardarRecordatorio(String userId, String canalId, String mensaje, long timestamp) {
        String sql = "INSERT INTO Recordatorios(usuario_id, canal_id, mensaje, tiempo_ejecucion) VALUES (?,?,?,?)";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, canalId);
            ps.setString(3, mensaje);
            ps.setLong(4, timestamp);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            logger.info("Error al guardar recordatorio: {}", e.getMessage());
            return false;
        }
    }

    public static void eliminarRecordatorio(int id){
        String op="delete from Recordatorios where id=?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(op)){
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static List<RecordatorioObj> obtenerRecordatoriosVencidos(long tiempoAhora) {
        List<RecordatorioObj> lista = new ArrayList<>();
        String sql = "SELECT id, usuario_id, canal_id, mensaje FROM Recordatorios WHERE tiempo_ejecucion <= ?";
        try (Connection conn = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, tiempoAhora);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new RecordatorioObj(
                            rs.getInt("id"),
                            rs.getString("usuario_id"),
                            rs.getString(PARAM_CANALID),
                            rs.getString("mensaje")
                    ));
                }
            }
        } catch (Exception e) {
            logger.info("Error al buscar recordatorios vencidos: {}", e.getMessage());
        }
        return lista;
    }
}