package aperez578.Notificaciones.Comandos;

import aperez578.ConexionBD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesBD {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionesBD.class);
    private static final Connection c;
    private static final String PARAM_TITULO="titulo";
    private static final String PARAM_TIEMPO="tiempo";
    private static final String PARAM_CREADORID="creador_id";
    private static final String PARAM_CANALID="canal_id";
    private static final String PARAM_ROLID="rol_id";
    private static final String PARAM_TIPOBOTON="tipoBoton";
    private static final String PARAM_OPCIONES="opciones";

    private NotificacionesBD(){}

    static {
        try {c = ConexionBD.getConexionBD().obtenerConexion();
        } catch (SQLException e) {throw new RuntimeException(e);}
    }

    public static boolean crearTarea(String titulo, long timestapmp, String idAutor, String idCanal, String mencionExtra,int tipoBoton,String opciones) {
        String op = "insert or replace into Evento(titulo,tiempo,creador_id,canal_id,rol_id,tipoBoton,opciones) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = ConexionBD.getConexionBD().obtenerConexion();
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

    public static List<Tarea> listarTareas(String idUser) {
        List<Tarea> tareaList = new ArrayList<>();
        String op = "select id, titulo, tiempo, creador_id, canal_id, rol_id, tipoBoton, opciones from Evento where creador_id=?";
        try (PreparedStatement psGuardar = c.prepareStatement(op)
        ) {
            psGuardar.setString(1, idUser);
            ResultSet rs = psGuardar.executeQuery();

            // 1. Añadimos el while para recorrer los resultados de forma segura
            while (rs.next()) {
                // 2. Usamos los nombres EXACTOS de las columnas de tu CREATE TABLE
                String titulo = rs.getString(PARAM_TITULO);
                long tiempo = rs.getLong(PARAM_TIEMPO);
                String creadorId = rs.getString(PARAM_CREADORID); // Antes tenías "idAutor"
                String canalId = rs.getString(PARAM_CANALID);     // Antes tenías "idCanal"
                String rolId = rs.getString(PARAM_ROLID); // 1. Sacamos el rol de la BD
                int tipoBoton=rs.getInt(PARAM_TIPOBOTON);
                String opciones=rs.getString(PARAM_OPCIONES);

                // 3. Creamos el objeto Tarea con los datos reales traducidos
                tareaList.add(new Tarea(titulo, tiempo, creadorId, canalId, rs.getInt("id"), rolId,tipoBoton,opciones));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public static void borrarTarea(int idTarea) {
        String op = "DELETE from Evento where id=?";
        try (PreparedStatement ps = c.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static List<Tarea> tareasAvisar(long tiempoInicio, long tiempoFin) {

        String op = "SELECT id, titulo, tiempo, creador_id, canal_id, rol_id, tipoBoton, opciones FROM Evento WHERE tiempo BETWEEN ? AND ?";
        List<Tarea> tareaList = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(op)) {
            ps.setLong(1, tiempoInicio);
            ps.setLong(2, tiempoFin);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String titulo = rs.getString(PARAM_TITULO);
                long tiempo = rs.getLong(PARAM_TITULO);
                String creadorId = rs.getString(PARAM_CREADORID); // Antes tenías "idAutor"
                String canalId = rs.getString(PARAM_CANALID);     // Antes tenías "idCanal"
                String rolId = rs.getString(PARAM_ROLID); // 1. Sacamos el rol de la BD
                int tipoBoton=rs.getInt(PARAM_TIPOBOTON);
                String opciones=rs.getString(PARAM_OPCIONES);
                // 3. Creamos el objeto Tarea con los datos reales traducidos
                tareaList.add(new Tarea(titulo, tiempo, creadorId, canalId, rs.getInt("id"), rolId,tipoBoton,opciones));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public static List<Tarea> listarTareasServer(String canalID) {
        List<Tarea> tareaList = new ArrayList<>();
        String op = "SELECT id, titulo, tiempo, creador_id, canal_id, rol_id, tipoBoton, opciones FROM Evento WHERE canal_id = ? ORDER BY tiempo ASC";
        try (PreparedStatement psGuardar = c.prepareStatement(op)
        ) {
            psGuardar.setString(1, canalID);
            ResultSet rs = psGuardar.executeQuery();

            // 1. Añadimos el while para recorrer los resultados de forma segura
            while (rs.next()) {
                // 2. Usamos los nombres EXACTOS de las columnas de tu CREATE TABLE
                String titulo = rs.getString(PARAM_TITULO);
                long tiempo = rs.getLong(PARAM_TIEMPO);
                String creadorId = rs.getString(PARAM_CREADORID); // Antes tenías "idAutor"
                String canalId = rs.getString(PARAM_CANALID);     // Antes tenías "idCanal"
                String rolId = rs.getString(PARAM_ROLID); // 1. Sacamos el rol de la BD
                int tipoBoton= rs.getInt(PARAM_TIPOBOTON);
                String opciones=rs.getString(PARAM_OPCIONES);
                // 3. Creamos el objeto Tarea con los datos reales traducidos
                tareaList.add(new Tarea(titulo, tiempo, creadorId, canalId, rs.getInt("id"), rolId,tipoBoton,opciones));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return tareaList;
    }

    public static boolean actualizarTitulo(int id, String titulo) {

        String op = "UPDATE Evento SET titulo = ? WHERE id = ?";
        try(PreparedStatement ps = c.prepareStatement(op)) {
            ps.setString(1, titulo); // El primer '?' ahora es el Texto
            ps.setInt(2, id);        // El segundo '?' es el ID
            return ps.executeUpdate()==1;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public static boolean actualizarTiempo(int id, long nuevoTimestamp) {
        String sql = "UPDATE Evento SET tiempo = ? WHERE id = ?";
        try (PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setLong(1, nuevoTimestamp);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public static void apuntarseTarea(int eventoid, String userId, String voto){

        String op = "INSERT OR REPLACE INTO Asistencia(tarea_id, usuario_id, voto) VALUES (?, ?, ?)";
        try (PreparedStatement ps=c.prepareStatement(op)){
            ps.setInt(1,eventoid);
            ps.setString(2,userId);
            ps.setString(3, voto);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static void desApuntareseEvento(int eventoid,String userId){
        String op="Delete from Asistencia where tarea_id=? and usuario_id=?";

        try ( PreparedStatement ps=c.prepareStatement(op)){
            ps.setInt(1,eventoid);
            ps.setString(2,userId);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    // 1. Busca una tarea específica por su ID para poder reconstruir su tarjeta
    public static Tarea obtenerTareaPorId(int idTarea) {
        String op = "SELECT id, titulo, tiempo, creador_id, canal_id, rol_id, tipoBoton, opciones FROM Evento WHERE id = ?";
        try ( PreparedStatement ps = c.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            ResultSet rs = ps.executeQuery();
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
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    // 2. Devuelve la lista de IDs de Discord de los usuarios apuntados al evento
    public static List<String> obtenerAsistentes(int idTarea) {
        List<String> usuarios = new ArrayList<>();
        String op = "SELECT usuario_id FROM Asistencia WHERE tarea_id = ?";
        try ( PreparedStatement ps = c.prepareStatement(op)) {
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
    public static List<String> obtenerTodosLosVotos(int idTarea) {
        List<String> votos = new ArrayList<>();
        String op = "SELECT voto FROM Asistencia WHERE tarea_id = ?";
        try (  PreparedStatement ps = c.prepareStatement(op)) {
            ps.setInt(1, idTarea);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) votos.add(rs.getString("voto"));

        } catch (Exception e) {logger.info(e.getMessage());}
        return votos;
    }

    public static void guardarCanalAlertas(String guildId, String canalId) {
        String op = "INSERT OR REPLACE INTO Configuracion(guild_id, canal_alertas_id) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(op)) {
            ps.setString(1, guildId);
            ps.setString(2, canalId);
            ps.executeUpdate();
        } catch (Exception e) { logger.info(e.getMessage()); }
    }

    public static String obtenerCanalAlertas(String guildId) {
        String op = "SELECT canal_alertas_id FROM Configuracion WHERE guild_id = ?";
        try ( PreparedStatement ps = c.prepareStatement(op)) {
            ps.setString(1, guildId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("canal_alertas_id");
        } catch (Exception e) { logger.info(e.getMessage()); }
        return null; // Si no está configurado, devolverá null
    }

    public static List<Tarea> listarTareasAsistidas(String userID) {
        List<Tarea> tareaList = new ArrayList<>();
        String sql = "SELECT e.* FROM Evento e INNER JOIN Asistencia a ON e.id = a.tarea_id WHERE a.usuario_id = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String titulo = rs.getString(PARAM_TITULO);
                long tiempo = rs.getLong(PARAM_TIEMPO);
                String creadorId = rs.getString(PARAM_CREADORID);
                String canalId = rs.getString(PARAM_CANALID);
                String rolId = rs.getString(PARAM_ROLID);
                int tipoBoton = rs.getInt(PARAM_TIPOBOTON);
                String opciones = rs.getString(PARAM_OPCIONES);

                tareaList.add(new Tarea(titulo, tiempo, creadorId, canalId, rs.getInt("id"), rolId, tipoBoton, opciones));
            }
        } catch (Exception e) {
            logger.info("Error al listar tareas asistidas: {}", e.getMessage());
        }
        return tareaList;
    }

    public static boolean guardarRecordatorio(String userId, String canalId, String mensaje, long timestamp) {
        String sql = "INSERT INTO Recordatorios(usuario_id, canal_id, mensaje, tiempo_ejecucion) VALUES (?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
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

        try ( PreparedStatement ps=c.prepareStatement(op)){
            ps.setInt(1,id);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static List<RecordatorioObj> obtenerRecordatoriosVencidos(long tiempoAhora) {
        List<RecordatorioObj> lista = new ArrayList<>();
        String sql = "SELECT id, usuario_id, canal_id, mensaje FROM Recordatorios WHERE tiempo_ejecucion <= ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
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
            logger.info("Error al buscar recordatorios vencidos: {}", e.getMessage());
        }
        return lista;
    }
}