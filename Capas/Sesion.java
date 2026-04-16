package Capas;

import PDU.*;

/**
 * Implementación de la Capa 5 (Sesión) del modelo OSI.
 * Se encarga de establecer, administrar y finalizar las conexiones de comunicación 
 * lógica entre los sistemas locales y remotos. En esta arquitectura, implementa 
 * el control de acceso verificando la legitimidad del receptor.
 */
public class Sesion extends CapaOSI {
    public Sesion(){
        super("SESION");
    }


    /**
     * Establecimiento de la sesión lógica (Flujo descendente).
     * @param pduPresentacion PDU proveniente de la capa superior.
     * @param hostOrigen Identificador del nodo transmisor.
     * @param hostDestino Identificador del nodo receptor esperado.
     * @return PDU (Datos) con la información de enrutamiento lógico adjunta.
     */
    public Datos encapsular(Datos pduPresentacion, String hostOrigen, String hostDestino) {
        // Generación de cabecera de sesión para el control del diálogo.
        String cabecera = String.format("[%s|origen=%s|destino=%s]", 
                                        NOMBRE_CAPA, hostOrigen, hostDestino);
        Datos pduSesion = new Datos(cabecera, pduPresentacion.getCompleto());
        
        return pduSesion;
    }


    /**
     * Validación y mantenimiento de la sesión lógica (Flujo ascendente).
     * @param pduSesion PDU recibida desde la capa de transporte.
     * @param nombreHostActual Identificador del nodo que está procesando la recepción.
     * @return PDU (Datos) con la carga útil limpia para la capa de presentación.
     * @throws RuntimeException Si se detecta una violación de seguridad o intercepción.
     */
    public Datos desencapsular(Datos pduSesion, String nombreHostActual) {
  
        String datosCompletos = pduSesion.getDatos();
        
        // Extracción y validación de la cabecera de sesión.
        int finCabecera = datosCompletos.indexOf("]");
        if (finCabecera == -1) {
            throw new RuntimeException("Error: No se encontró cabecera de SESION");
        }
        
        String cabecera = datosCompletos.substring(0, finCabecera + 1);
        String destinoEsperado = "";
        
        // Análisis de la cadena de metadatos para extraer el parámetro "destino".
        String[] partes = cabecera.split("\\|");
        for(String parte : partes) {
            if(parte.startsWith("destino=")) {
                destinoEsperado = parte.substring(8);
                // Quitar el ']' del final si existe
                if (destinoEsperado.endsWith("]")) {
                    destinoEsperado = destinoEsperado.substring(0, destinoEsperado.length() - 1);
                }
            }
        }

        // Lógica de validación de seguridad (Control de acceso).
        // Si el host físico actual no coincide con el destinatario lógico de la sesión,
        // se interrumpe inmediatamente la cadena de desencapsulación
        if (!destinoEsperado.equals(nombreHostActual)) {
            throw new RuntimeException("ERROR DE SESIÓN: Este paquete estaba dirigido a '" + 
                                       destinoEsperado + "' pero fue interceptado por '" + nombreHostActual + "'.");
        }


        // Pasar los datos sin la cabecera de SESION
        String datosParaPresentacion = datosCompletos.substring(finCabecera + 2);
        return new Datos("", datosParaPresentacion);
    }
}