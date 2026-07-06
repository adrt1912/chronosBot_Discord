package aperez578.Notificaciones.Comandos;

public class RecordatorioObj {

    private int id;
    private  String usuarioid;
    private String canalid;
    private String mensaje;

    public RecordatorioObj(int id,String usuarioid,String canalid,String mensaje){
        this.id=id;
        this.usuarioid=usuarioid;
        this.canalid=canalid;
        this.mensaje=mensaje;
    }

    public int getId() {
        return id;
    }

    public String getCanalid() {
        return canalid;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getUsuarioid() {
        return usuarioid;
    }
}
