package aperez578.experiencia.comandos;

import aperez578.ConexionBD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExperienciaBD {

    private static final Logger logger = LoggerFactory.getLogger(ExperienciaBD.class);

    private ExperienciaBD(){}

    private static final String PARAM_NIVEL = "nivel";

    public static int[] ganarXP(String userId, String guildId, int xpGanada) {
        // 1. Intentamos insertar o actualizar la XP del usuario
        String sqlUpsert = "INSERT INTO niveles (user_id, guild_id, xp) VALUES (?, ?, ?) " +
                "ON CONFLICT(user_id, guild_id) DO UPDATE SET xp = xp + ?;";

        // 2. Consultamos cómo ha quedado su XP y su nivel actual
        String sqlSelect = "SELECT xp, nivel FROM niveles WHERE user_id = ? AND guild_id = ?;";

        // 3. Si sube de nivel, actualizamos el campo 'nivel'
        String sqlUpdateNivel = "UPDATE niveles SET nivel = ? WHERE user_id = ? AND guild_id = ?;";

        try (Connection c=ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement pUpsert = c.prepareStatement(sqlUpsert);
             PreparedStatement pSelect = c.prepareStatement(sqlSelect);
             PreparedStatement pUpdate = c.prepareStatement(sqlUpdateNivel)) {

            // Ejecutamos la suma de XP
            pUpsert.setString(1, userId);
            pUpsert.setString(2, guildId);
            pUpsert.setInt(3, xpGanada);
            pUpsert.setInt(4, xpGanada);
            pUpsert.executeUpdate();

            // Miramos los datos actuales
            pSelect.setString(1, userId);
            pSelect.setString(2, guildId);
            try (ResultSet rs = pSelect.executeQuery()) {
                if (rs.next()) {
                    int xpActual = rs.getInt("xp");
                    int nivelActual = rs.getInt(PARAM_NIVEL);

                    int xpNecesaria = nivelActual * 200;

                    if (xpActual >= xpNecesaria) {
                        int nuevoNivel = nivelActual + 1;

                        pUpdate.setInt(1, nuevoNivel);
                        pUpdate.setString(2, userId);
                        pUpdate.setString(3, guildId);
                        pUpdate.executeUpdate();

                        return new int[]{nuevoNivel, 1}; // Retorna [Nuevo Nivel, SÍ ha subido]
                    }
                    return new int[]{nivelActual, 0}; // Retorna [Nivel Actual, NO ha subido]
                }
            }
        } catch (SQLException e) {
            logger.info("❌ Error en el sistema de XP: {}", e.getMessage());
        }
        return new int[]{1, 0};
    }

    public static int[] obtenerPerfilNivel(String userId,String guildId){

        String sql="select xp,nivel from niveles where user_id=? and guild_id=?;";

        try (Connection c=ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps=c.prepareStatement(sql)){

            ps.setString(1,userId);
            ps.setString(2,guildId);

            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                int xp=rs.getInt("xp");
                int nivel=rs.getInt(PARAM_NIVEL);
                return new int[]{xp,nivel};
            }

        } catch (Exception e) {
            logger.info("❌ Error al obtener el perfil de nivel: {}", e.getMessage());
        }
        return new int[]{0,1};
    }

    public static java.util.List<String[]> obtenerTopNiveles(String guildId) {
        String sql = "SELECT user_id, xp, nivel FROM niveles WHERE guild_id = ? ORDER BY xp DESC LIMIT 10;";
        java.util.List<String[]> topList = new java.util.ArrayList<>();

        try (Connection c=ConexionBD.getConexionBD().obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, guildId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    topList.add(new String[]{
                            rs.getString("user_id"),
                            String.valueOf(rs.getInt("xp")),
                            String.valueOf(rs.getInt(PARAM_NIVEL))
                    });
                }
            }
        } catch (SQLException e) {
            logger.info("❌ Error al obtener el top de niveles: {}", e.getMessage());
        }
        return topList;
    }
}