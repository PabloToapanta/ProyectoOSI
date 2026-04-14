package Capas;

import PDU.*;
import java.util.ArrayList;
import java.util.List;

public class Enlace {
    private static final String NOMBRE_CAPA = "Enlace";
    private static final String MAC_ORIGEN = "AA:BB:CC:DD:EE:01";
    private static final String MAC_DESTINO = "AA:BB:CC:DD:EE:02";

    /**
     * Encapsula cada PDU de Red añadiendo cabecera MAC y FCS.
     */
    public List<PDU> encapsular(List<PDU> pdusRed) {
        List<PDU> tramas = new ArrayList<>();

        System.out.printf("ENVIO DESDE ENLACE (Capa 2):\nAgregando encabezado MAC y FCS a %d datagramas...\n", 
            pdusRed.size());

        for (int i = 0; i < pdusRed.size(); i++) {
            PDU pduRed = pdusRed.get(i);
            String datosRed = pduRed.getCompleto();
            
            int fcs = calcularFCS(datosRed);
            
            String cabecera = String.format("[%s|MAC_origen=%s|MAC_destino=%s|FCS=%d]",
                NOMBRE_CAPA, MAC_ORIGEN, MAC_DESTINO, fcs);
            
            tramas.add(new PDU(cabecera, datosRed));

            System.out.printf("  Trama %d: FCS calculado=%d, Tamaño trama=%d bytes\n", 
                i + 1, fcs, cabecera.length() + datosRed.length() + 1);
        }

        System.out.printf("Total de tramas generadas: %d\n\n", tramas.size());
        return tramas;
    }

    /**
     * Desencapsula eliminando cabecera MAC y verificando FCS.
     */
    public List<PDU> desencapsular(List<PDU> tramas) {
        List<PDU> pdusRed = new ArrayList<>();

        System.out.printf("RECEPCION DESDE ENLACE (Capa 2):\nVerificando FCS y removiendo MAC de %d tramas...\n", 
            tramas.size());

        for (int i = 0; i < tramas.size(); i++) {
            PDU trama = tramas.get(i);
            String tramaCompleta = trama.getCompleto();
            
            // Extraer FCS de la cabecera
            String cabecera = trama.getCabecera();
            int indiceFCS = cabecera.indexOf("FCS=") + "FCS=".length();
            int indiceFCSFin = cabecera.indexOf("]");
            int fcsRecibido = Integer.parseInt(cabecera.substring(indiceFCS, indiceFCSFin));
            
            // Datos de Red (payload)
            String datosRed = trama.getDatos();
            
            // Verificar FCS
            int fcsCalculado = calcularFCS(datosRed);
            
            if (fcsRecibido != fcsCalculado) {
                throw new RuntimeException(
                    String.format("Error en trama %d: FCS no coincide. Recibido: %d, Calculado: %d. " +
                                "Datos corrompidos detectados.",
                        i + 1, fcsRecibido, fcsCalculado)
                );
            }
            
            // Reconstruir PDU de Red (los datos contienen la cabecera de Red + payload)
            pdusRed.add(new PDU("", datosRed));
            System.out.printf("  Trama %d: FCS válido (%d), Datagrama extraído\n", i + 1, fcsRecibido);
        }

        System.out.printf("Todas las tramas verificadas correctamente. Datagramas: %d\n\n", 
            pdusRed.size());
        return pdusRed;
    }

    private int calcularFCS(String datos) {
        return datos.length() * 123 + 456;
    }
}