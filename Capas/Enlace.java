package Capas;

import PDU.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementación de la Capa 2 (Enlace de Datos) del modelo OSI.
 * Con responsabilida de creacion de tramas y control basico de errores
 */
public class Enlace extends CapaOSI {
    public Enlace(){
        super("ENLACE");
    }


    /**
     * Proceso de entramado y direccionamiento físico (Flujo descendente).
     * @param paquetesRed Lista de PDUs de la capa de red (Paquetes).
     * @param MAC_ORIGEN Dirección física de la interfaz de red emisora.
     * @param MAC_DESTINO Dirección física de la interfaz de red receptora.
     * @return Lista de Tramas listas para ser enviadas al medio físico.
     */ 
    public List<Trama> encapsular(List<Paquete> paquetesRed, String MAC_ORIGEN, String MAC_DESTINO) {
        List<Trama> tramas = new ArrayList<>();

        for (int i = 0; i < paquetesRed.size(); i++) {
            PDU pduRed = paquetesRed.get(i);
            String datosRed = pduRed.getCompleto();
            
            // Calculamos el código de integridad
            int integridad = calcularIntegridad(datosRed);
            
            //Inyeccion de cabecera
            String cabecera = String.format("[%s|MAC_origen=%s|MAC_destino=%s|Integridad=%d]",
                NOMBRE_CAPA, MAC_ORIGEN, MAC_DESTINO, integridad);
            
            // Creacion de la lista
            tramas.add(new Trama(cabecera, datosRed));

        }


        return tramas;
    }

    /**
     * Proceso de validación de tramas y detección de errores (Flujo ascendente).
     * @param tramas Lista de tramas recibidas desde la capa física.
     * @return Lista de Paquetes validados y libres de cabeceras físicas.
     * @throws RuntimeException Si se detecta corrupción en los datos durante la transmisión.
     */
    public List<Paquete> desencapsular(List<Trama> tramas) {
        List<Paquete> pdusRed = new ArrayList<>();


        for (int i = 0; i < tramas.size(); i++) {
            PDU trama = tramas.get(i);
            String tramaCompleta = trama.getCompleto();
            
            // Extraer el código de Integridad de la cabecera
            String cabecera = trama.getCabecera();

            // Buscamos la palabra "Integridad=" 
            int indiceIntegridad = cabecera.indexOf("Integridad=") + "Integridad=".length();
            int indiceIntegridadFin = cabecera.indexOf("]");
            int integridadRecibida = Integer.parseInt(cabecera.substring(indiceIntegridad, indiceIntegridadFin));
            
            // Datos de Red (payload)
            String datosRed = trama.getDatos();
            
            // Verificar Integridad
            int integridadCalculada = calcularIntegridad(datosRed);
            
            if (integridadRecibida != integridadCalculada) {
                throw new RuntimeException(
                    String.format("Error en trama %d: Código de integridad no coincide. Recibido: %d, Calculado: %d. " +
                                "Datos corrompidos detectados.",
                        i + 1, integridadRecibida, integridadCalculada)
                );
            }
            
            // Reconstruir PDU de Red
            pdusRed.add(new Paquete("", datosRed));
        }


        return pdusRed;
    }

    /**
     * Metodo para calcular el código de integridad..
     * @param datos Cadena de datos sobre la cual calcular el hash.
     * @return Valor entero representativo de la estructura exacta de los datos.
     */
    private int calcularIntegridad(String datos) {
        return datos.length() * 123 + 456;
    }
}