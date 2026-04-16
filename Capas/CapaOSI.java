package Capas;
/**
 * Clase abstracta base que define el contrato y la estructura común para todas 
 * las capas del modelo OSI en la simulación. 
 * Aplica el principio de abstracción y evita la duplicación de código
 */
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
