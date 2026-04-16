package Capas;
import PDU.*;

/**
 * Implementación de la Capa 7 (Aplicación) del modelo OSI.
 * Utiliza herencia extendiendo de CapaOSI. Su responsabilidad es servir de 
 * interfaz directa con los datos del usuario, validando las restricciones 
 * del sistema e integrando los metadatos de nivel de aplicación.
 */
public class Aplicacion extends CapaOSI {
    
    // Llamada al constructor de la superclase para inicializar el estado del objeto.
    public Aplicacion(){
        super("APLICACION");
    }
    // Constante que define el límite de transmisión permitido por la simulación.
    private static final int MAX_BYTE_SIZE = 500;

    /**
     * Proceso de encapsulación (Flujo descendente).
     * @param mensajeUsuario Datos crudos ingresados por el sistema emisor.
     * @return PDU (Datos) con la cabecera conceptual de aplicación.
     */
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


    /**
     * Proceso de desencapsulación (Flujo ascendente).
     * @param pduAplicacion Objeto PDU proveniente de la capa inferior.
     * @return La informacion decodificado para el usuario final.
     */
    public String desencapsular(Datos pduAplicacion) {
        String datosCompletos = pduAplicacion.getDatos();
        String mensajeFinal = datosCompletos;

        // Lógica de extracción de carga útil: se delimita la cabecera
        // para aislar y retornar únicamente la información original.

        int finCabecera = datosCompletos.indexOf("]");
  
        if (finCabecera != -1) {
            mensajeFinal = datosCompletos.substring(finCabecera + 2); 
        }
        return mensajeFinal;
    }
}