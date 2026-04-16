package PDU;

public class Paquete extends PDU {
    private static final long serialVersionUID = 1L;

    public Paquete(String cabecera, String datos) {
        super(cabecera, datos);
    }
}