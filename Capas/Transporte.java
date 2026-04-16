package Capas;

import PDU.*;
import java.util.List;


public class Transporte extends CapaOSI {
    
    public Transporte(){
        super("TRANSPORTE");
    }


    public Segmento encapsular(Datos DatosSesion,int PUERTO_ORIGEN, int PUERTO_DESTINO) {
        String cabecera = String.format("[%s|pOrigen=%d|pDestino=%d]", 
                                        NOMBRE_CAPA, PUERTO_ORIGEN, PUERTO_DESTINO);
        Segmento pduTransporte = new Segmento(cabecera, DatosSesion.getCompleto());
        
        
        return pduTransporte;
    }

    public Datos desencapsular(List<Segmento> segmentos) {

        // Reensamblar los datos
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