package Capas;

import PDU.*;
import java.util.ArrayList;
import java.util.List;

public class Enlace {
    private static final String NOMBRE_CAPA = "Enlace";
    private static final String MAC_ORIGEN = "AA:BB:CC:DD:EE:01";
    private static final String MAC_DESTINO = "AA:BB:CC:DD:EE:02";

    /**
     * Encapsula cada PDU de Red añadiendo cabecera MAC y código de integridad.
     */
    public List<PDU> encapsular(List<PDU> pdusRed) {
        List<PDU> tramas = new ArrayList<>();

        System.out.printf("ENVIO DESDE ENLACE (Capa 2):\nAgregando encabezado MAC y código de integridad a %d datagramas...\n", 
            pdusRed.size());

        for (int i = 0; i < pdusRed.size(); i++) {
            PDU pduRed = pdusRed.get(i);
            String datosRed = pduRed.getCompleto();
            
            // Calculamos el código de integridad
            int integridad = calcularIntegridad(datosRed);
            
            
            String cabecera = String.format("[%s|MAC_origen=%s|MAC_destino=%s|Integridad=%d]",
                NOMBRE_CAPA, MAC_ORIGEN, MAC_DESTINO, integridad);
            
            tramas.add(new PDU(cabecera, datosRed));

            System.out.printf("  Trama %d: Integridad calculada=%d, Tamaño trama=%d bytes\n", 
                i + 1, integridad, cabecera.length() + datosRed.length() + 1);
        }

        System.out.printf("Total de tramas generadas: %d\n\n", tramas.size());
        return tramas;
    }

    /**
     * Desencapsula eliminando cabecera MAC y verificando el código de integridad.
     */
    public List<PDU> desencapsular(List<PDU> tramas) {
        List<PDU> pdusRed = new ArrayList<>();

        System.out.printf("RECEPCION DESDE ENLACE (Capa 2):\nVerificando código de integridad y removiendo MAC de %d tramas...\n", 
            tramas.size());

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
            pdusRed.add(new PDU("", datosRed));
            System.out.printf("  Trama %d: Integridad válida (%d), Datagrama extraído\n", i + 1, integridadRecibida);
        }

        System.out.printf("Todas las tramas verificadas correctamente. Datagramas: %d\n\n", 
            pdusRed.size());
        return pdusRed;
    }

    private int calcularIntegridad(String datos) {
        return datos.length() * 123 + 456;
    }
}