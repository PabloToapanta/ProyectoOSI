package Capas;
import PDU.*;
public class Aplicacion extends CapaOSI {
    
    public Aplicacion(){
        super("APLICACION");
    }
    private static final int MAX_BYTE_SIZE = 500;
    public Datos encapsular(String mensajeUsuario) {
        if (mensajeUsuario.getBytes().length > MAX_BYTE_SIZE) {
            throw new IllegalArgumentException(
                String.format("Error: El mensaje supera el tamaño máximo de %d bytes. Tamaño actual: %d bytes",
                    MAX_BYTE_SIZE, mensajeUsuario.getBytes().length)
            );
        }
        String cabecera = String.format("[%s|Tipo Dato: Texto]", NOMBRE_CAPA);
        Datos pduAplicacion = new Datos(cabecera, mensajeUsuario);
        return pduAplicacion;
    }
    public String desencapsular(Datos pduAplicacion) {
        String datosCompletos = pduAplicacion.getDatos();
        String mensajeFinal = datosCompletos;
        // Buscamos dónde termina la cabecera de APLICACION
        int finCabecera = datosCompletos.indexOf("]");
        // Extraemos solo el mensaje puro, saltando el "]|"
        if (finCabecera != -1) {
            mensajeFinal = datosCompletos.substring(finCabecera + 2); 
        }
        return mensajeFinal;
    }
}