package Capas;

import PDU.*;
import java.util.ArrayList;
import java.util.List;

public class Enlace extends CapaOSI {
    public Enlace(){
        super("ENLACE");
    }


    /**
     * Encapsula cada PDU de Red añadiendo cabecera MAC y código de integridad.
     */
    public List<Trama> encapsular(List<Paquete> paquetesRed, String MAC_ORIGEN, String MAC_DESTINO) {
        List<Trama> tramas = new ArrayList<>();

        for (int i = 0; i < paquetesRed.size(); i++) {
            PDU pduRed = paquetesRed.get(i);
            String datosRed = pduRed.getCompleto();
            
            // Calculamos el código de integridad
            int integridad = calcularIntegridad(datosRed);
            
            
            String cabecera = String.format("[%s|MAC_origen=%s|MAC_destino=%s|Integridad=%d]",
                NOMBRE_CAPA, MAC_ORIGEN, MAC_DESTINO, integridad);
            
            tramas.add(new Trama(cabecera, datosRed));

        }


        return tramas;
    }

    /**
     * Desencapsula eliminando cabecera MAC y verificando el código de integridad.
     */
    public List<Paquete> desencapsular(List<Trama> tramas) {
        List<Paquete> pdusRed = new ArrayList<>();


        for (int i = 0; i < tramas.size(); i++) {
            PDU trama = tramas.get(i);
            String tramaCompleta = trama.getCompleto();
            
            // Extraer el código de Integridad de la cabecera
            String cabecera = trama.getCabecera();
            // Buscamos la palabra "Integridad=" en lugar de "FCS="
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

    private int calcularIntegridad(String datos) {
        return datos.length() * 123 + 456;
    }
}