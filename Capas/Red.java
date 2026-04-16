package Capas;

import PDU.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la Capa 3 (Red) del modelo OSI.
 * Es responsable del  direccionamiento lógico (IP) y la fragmentación
 * de los datos. Adapta el tamaño de la carga útil a las restricciones físicas 
 * de la red simulando el concepto de Unidad Máxima de Transferencia (MTU).
 */
public class Red extends CapaOSI{
    public Red(){
        super("RED");
    }

    // Definición de la Unidad Máxima de Transferencia (MTU) simulada.
    // Obliga a que los segmentos grandes se dividan en fragmentos más pequeños.
    private static final int TAMANO_PAQUETE = 50;


    /**
     * Proceso de fragmentación y direccionamiento lógico (Flujo descendente).
     * @param segmentoTransporte Segmento íntegro proveniente de la capa 4.
     * @param IP_ORIGEN Dirección lógica del host emisor.
     * @param IP_DESTINO Dirección lógica del host receptor.
     * @return Lista de Paquetes (PDUs fragmentados con cabeceras individuales).
     */
    public List<Paquete> encapsular(Segmento segmentoTransporte, String IP_ORIGEN,String IP_DESTINO) {
        List<Paquete> paquetes = new ArrayList<>();
        String datosTransporte = segmentoTransporte.getCompleto();

        // Cálculo matemático para determinar la cantidad exacta de paquetes necesarios.
        int totalPaquetes = (int) Math.ceil((double) datosTransporte.length() / TAMANO_PAQUETE);

        // Bucle de fragmentación de datos
        for (int i = 0; i < totalPaquetes; i++) {
            // Delimitación de índices para extraer el trozo exacto de información.
            int inicio = i * TAMANO_PAQUETE;
            int fin = Math.min(inicio + TAMANO_PAQUETE, datosTransporte.length());
            String trozo = datosTransporte.substring(inicio, fin);

            // Inyección de la cabecera
            String cabecera = String.format("[%s|origen=%s|destino=%s|paquete=%d/%d]",
                    NOMBRE_CAPA, IP_ORIGEN, IP_DESTINO, i + 1, totalPaquetes);
                    // Instanciación del objeto intermedio correspondiente a la capa de red
            paquetes.add(new Paquete(cabecera, trozo));
        }
        return paquetes;
    }

    /**
    * Proceso de extracción de cabeceras de enrutamiento (Flujo ascendente).
    * @param paquetesRecibidos Lista de paquetes validados por la capa de enlace.
    * @return Lista de Segmentos puros preparados para el reensamblado en la capa superior.
    */
   public List<Segmento> desencapsular(List<Paquete> paquetesRecibidos) {
   
    List<Segmento> fragmentos = new ArrayList<>();
    for (Paquete paquete : paquetesRecibidos) {
        String contenido = paquete.getDatos();
         // Iteración para remover la cabecera de cada paquete
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