package Capas;

import PDU.*;
import java.util.ArrayList;
import java.util.List;

public class Red {
    private static final String NOMBRE_CAPA = "RED";
    private static final String IP_ORIGEN = "192.168.0.10";
    private static final String IP_DESTINO = "192.168.0.12";
    private static final int TAMANO_PAQUETE = 50;

    public List<PDU> encapsular(PDU pduTransporte) {
        List<PDU> paquetes = new ArrayList<>();
        String datosTransporte = pduTransporte.getCompleto();
        int totalPaquetes = (int) Math.ceil((double) datosTransporte.length() / TAMANO_PAQUETE);

        System.out.println("ENVIO DESDE " + NOMBRE_CAPA + " (Capa 3):");
        System.out.println("Fragmentando datos en " + totalPaquetes + " paquetes y agregando IPs");

        for (int i = 0; i < totalPaquetes; i++) {
            int inicio = i * TAMANO_PAQUETE;
            int fin = Math.min(inicio + TAMANO_PAQUETE, datosTransporte.length());
            String trozo = datosTransporte.substring(inicio, fin);

            String cabecera = String.format("[%s|origen=%s|destino=%s|paquete=%d/%d]",
                    NOMBRE_CAPA, IP_ORIGEN, IP_DESTINO, i + 1, totalPaquetes);
            paquetes.add(new PDU(cabecera, trozo));

            System.out.println("  Paquete " + (i + 1) + " generado correctamente.");
        }
        System.out.println();
        return paquetes;
    }

   public List<PDU> desencapsular(List<PDU> paquetesRecibidos) {
    System.out.println("RECEPCION DESDE " + NOMBRE_CAPA + " (Capa 3):");
    System.out.println("Procesando " + paquetesRecibidos.size() + " paquetes y validando IPs...");
    
    List<PDU> fragmentos = new ArrayList<>();
    for (PDU paquete : paquetesRecibidos) {
        String contenido = paquete.getDatos();
        // Buscamos donde termina la cabecera de RED para extraer solo lo que sigue
        int finCabeceraRed = contenido.indexOf("]");
        if (finCabeceraRed != -1) {
            // Extraemos el contenido después de "]|"
            String datosPuros = contenido.substring(finCabeceraRed + 2);
            fragmentos.add(new PDU("", datosPuros));
        }
    }
    
    System.out.println("Paquetes procesados. Cabeceras RED removidas.\n");
    return fragmentos;
}
}