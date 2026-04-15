package Capas;
import PDU.*;
public class Aplicacion {
    private static final String NOMBRE_CAPA = "APLICACION";
    private static final int MAX_BYTE_SIZE = 500;

    public PDU encapsular(String mensajeUsuario) {
        if (mensajeUsuario.getBytes().length > MAX_BYTE_SIZE) {
            throw new IllegalArgumentException(
                String.format("Error: El mensaje supera el tamaño máximo de %d bytes. Tamaño actual: %d bytes",
                    MAX_BYTE_SIZE, mensajeUsuario.getBytes().length)
            );
        }

        String cabecera = String.format("[%s|Enviar mensaje]", NOMBRE_CAPA);
        PDU pduAplicacion = new PDU(cabecera, mensajeUsuario);

        System.out.printf("ENVIO DE APLICACION (Capa 7):\nDato original: %s\nTamaño: %d bytes\nCabecera añadida: %s\n\n",
            mensajeUsuario, mensajeUsuario.getBytes().length, cabecera);
        return pduAplicacion;
    }

    public String desencapsular(PDU pduAplicacion) {
        String datosCompletos = pduAplicacion.getDatos();
        String mensajeFinal = datosCompletos;
        
        // Buscamos dónde termina la cabecera de APLICACION
        int finCabecera = datosCompletos.indexOf("]");
        
        // Extraemos solo el mensaje puro, saltando el "]|"
        if (finCabecera != -1) {
            mensajeFinal = datosCompletos.substring(finCabecera + 2); 
        }
        
        System.out.printf("RECEPCION DESDE APLICACION (Capa 7):\nPaquete recibido: %s\nDato entregado: %s\n\n",
            datosCompletos, mensajeFinal);
            
        return mensajeFinal;
    }
}