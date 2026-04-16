package Capas;

public abstract class CapaOSI {
    protected String NOMBRE_CAPA;

    // Constructor que obligará a cada capa hija a definirse
    public CapaOSI(String nombreCapa) {
        this.NOMBRE_CAPA = nombreCapa;
    }

    public String getNOMBRE_CAPA() {
        return NOMBRE_CAPA;
    }
}
