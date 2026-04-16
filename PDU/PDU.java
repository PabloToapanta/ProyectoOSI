package PDU;

import java.io.Serializable;

/**
 * Clase PDU (Protocol Data Unit)
 * Representa la unidad de datos que se pasa entre las capas del modelo OSI.
 * Contiene la cabecera de la capa y los datos (payload).
 */
public class PDU implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String cabecera;
    protected String datos;
    
    public PDU(String cabecera, String datos) {
        this.cabecera = cabecera;
        this.datos = datos;
    }
    
    public String getCabecera() {
        return cabecera;
    }
    
    public String getDatos() {
        return datos;
    }
    
    public void setCabecera(String cabecera) {
        this.cabecera = cabecera;
    }
    
    public void setDatos(String datos) {
        this.datos = datos;
    }
    
    /**
     * Retorna la representación completa de la PDU (cabecera + datos)
     */
    public String getCompleto() {
        return cabecera + "|" + datos;
    }
    
    @Override
    public String toString() {
        return getCompleto();
    }
}