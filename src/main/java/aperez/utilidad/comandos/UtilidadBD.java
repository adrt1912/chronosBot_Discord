package aperez.utilidad.comandos;

import aperez.ConexionBD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilidadBD {

    private final static Logger logger = LoggerFactory.getLogger(UtilidadBD.class);
    private final static Connection c;

    private UtilidadBD(){}

    static {
        try {c = ConexionBD.getConexionBD().obtenerConexion();
        } catch (SQLException e) {throw new RuntimeException(e);}
    }

    public static int registrarAdvertencia(String userId, String guildId, String razon, String modId) {
        String queryInsert = "INSERT INTO advertencias(user_id, guild_id, razon, mod_id) VALUES(?, ?, ?, ?);";
        String queryCount = "SELECT COUNT(*) FROM advertencias WHERE user_id = ? AND guild_id = ?;";
        int totalAvisos = 0;

        // Usamos try-with-resources para cerrar automáticamente los statements y cuidar la memoria
        try (PreparedStatement pstmtInsert = c.prepareStatement(queryInsert);
             PreparedStatement pstmtCount = c.prepareStatement(queryCount)) {

            // 1. Guardamos la nueva amonestación
            pstmtInsert.setString(1, userId);
            pstmtInsert.setString(2, guildId);
            pstmtInsert.setString(3, razon);
            pstmtInsert.setString(4, modId);
            pstmtInsert.executeUpdate();

            // 2. Contamos cuántas lleva ya acumuladas en este servidor
            pstmtCount.setString(1, userId);
            pstmtCount.setString(2, guildId);

            try (ResultSet rs = pstmtCount.executeQuery()) {
                if (rs.next()) totalAvisos = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.info("❌ Error al registrar la advertencia en la base de datos: {}", e.getMessage());
        }
        return totalAvisos;
    }

    public static void resetarAdvertencias(String userId, String guildId){
        String sql = "DELETE FROM advertencias WHERE user_id = ? AND guild_id = ?;";
        try ( PreparedStatement ps=c.prepareStatement(sql)){

            ps.setString(1,userId);
            ps.setString(2,guildId);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
}
