package aperez.economia.comandos;

import aperez.ConexionBD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class EconomiaBD {

    private static final Logger logger = LoggerFactory.getLogger(EconomiaBD.class);

    private EconomiaBD(){}

    public static long[] obtenerPerfilEconomia(String userId) {
        String sql = "SELECT monedas, ultimo_trabajo FROM Economia WHERE usuario_id = ?";
        try (Connection c = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new long[]{rs.getLong("monedas"), rs.getLong("ultimo_trabajo")};
        } catch (Exception e) {
            logger.info("Error al obtener perfil económico: {}", e.getMessage());
        }
        return new long[]{0, 0};
    }

    public static boolean actualizarEconomia(String userId, long nuevasMonedas, long nuevoTimestampTrabajo) {
        String sql = "INSERT OR REPLACE INTO Economia(usuario_id, monedas, ultimo_trabajo) VALUES (?,?,?)";
        try (Connection c = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setLong(2, nuevasMonedas);
            ps.setLong(3, nuevoTimestampTrabajo);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public static Map<String,Long> obtenerTopEconomida() {
        LinkedHashMap<String,Long> top = new LinkedHashMap<>();
        String sql = "SELECT usuario_id, monedas FROM Economia ORDER BY monedas DESC LIMIT 10";
        try (Connection c = ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                top.put(rs.getString("usuario_id"), rs.getLong("monedas"));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return top;
    }
}