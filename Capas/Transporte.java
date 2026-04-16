package Capas;

import PDU.*;
import java.util.List;

/**
 * Implementación de la Capa 4 (Transporte) del modelo OSI.
 * Su responsabilidad es garantizar la comunicación de extremo a extremo (end-to-end) 
 * entre los hosts, gestionando el direccionamiento de procesos (puertos lógicos) 
 * y el reensamblado de la información fragmentada proveniente de las capas inferiores.
 */
public class Transporte extends CapaOSI {
    
    public Transporte(){
        super("TRANSPORTE");
    }


    /**
     * Proceso de encapsulación y multiplexación (Flujo descendente).
     * @param DatosSesion PDU proveniente de la capa de sesión.
     * @param PUERTO_ORIGEN Puerto lógico de la aplicación emisora.
     * @param PUERTO_DESTINO Puerto lógico del servicio destino.
     * @return Segmento con la cabecera de transporte adjunta.
     */
    public Segmento encapsular(Datos DatosSesion,int PUERTO_ORIGEN, int PUERTO_DESTINO) {

        // Asignación de puertos para el direccionamiento a nivel de proceso.
        // Esto permite la multiplexación de múltiples aplicaciones en un mismo host.
        String cabecera = String.format("[%s|pOrigen=%d|pDestino=%d]", 
                                        NOMBRE_CAPA, PUERTO_ORIGEN, PUERTO_DESTINO);

        Segmento pduTransporte = new Segmento(cabecera, DatosSesion.getCompleto());
        
        
        return pduTransporte;
    }



    /**
     * Proceso de reensamblado y desencapsulación (Flujo ascendente).
     * @param segmentos Lista de fragmentos recibidos desde la capa de Red.
     * @return Datos unificado y restaurado para la capa de sesión.
     */
    public Datos desencapsular(List<Segmento> segmentos) {

        // Proceso de reensamblado secuencial: se concatenan los fragmentos 
        // para reconstruir el Segmento original en su totalidad.
        StringBuilder datosReensamblados = new StringBuilder();
        for (Segmento segmento : segmentos) {
            datosReensamblados.append(segmento.getDatos());
        }
        
        
        String datosCompletos = datosReensamblados.toString();
        
        // Los datos reensamblados empiezan con [TRANSPORTE|...]
        // Buscar el fin de la cabecera de TRANSPORTE
        int finCabeceraTransporte = datosCompletos.indexOf("]");
        if (finCabeceraTransporte == -1) {
            throw new RuntimeException("Error: Formato de datos incorrecto");
        }
        

        String datosSesion = datosCompletos.substring(finCabeceraTransporte + 2); // +2 para saltar "]|"
        
        Datos DatosSesion = new Datos("", datosSesion);
        

        
        return DatosSesion;
    }
}