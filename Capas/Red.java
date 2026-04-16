package Capas;

import PDU.*;
import java.util.ArrayList;
import java.util.List;

public class Red extends CapaOSI{
    public Red(){
        super("RED");
    }

    private static final int TAMANO_PAQUETE = 50;

    public List<Paquete> encapsular(Segmento segmentoTransporte, String IP_ORIGEN,String IP_DESTINO) {
        List<Paquete> paquetes = new ArrayList<>();
        String datosTransporte = segmentoTransporte.getCompleto();
        int totalPaquetes = (int) Math.ceil((double) datosTransporte.length() / TAMANO_PAQUETE);


        for (int i = 0; i < totalPaquetes; i++) {
            int inicio = i * TAMANO_PAQUETE;
            int fin = Math.min(inicio + TAMANO_PAQUETE, datosTransporte.length());
            String trozo = datosTransporte.substring(inicio, fin);

            String cabecera = String.format("[%s|origen=%s|destino=%s|paquete=%d/%d]",
                    NOMBRE_CAPA, IP_ORIGEN, IP_DESTINO, i + 1, totalPaquetes);
            paquetes.add(new Paquete(cabecera, trozo));
        }
        return paquetes;
    }

   public List<Segmento> desencapsular(List<Paquete> paquetesRecibidos) {
   
    List<Segmento> fragmentos = new ArrayList<>();
    for (Paquete paquete : paquetesRecibidos) {
        String contenido = paquete.getDatos();
        // Buscamos donde termina la cabecera de RED para extraer solo lo que sigue
        int finCabeceraRed = contenido.indexOf("]");
        if (finCabeceraRed != -1) {
            // Extraemos el contenido después de "]|"
            String datosPuros = contenido.substring(finCabeceraRed + 2);
            fragmentos.add(new Segmento("", datosPuros));
        }
    }
    
    return fragmentos;
}
}