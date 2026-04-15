package Capas;

import PDU.*;
import java.util.List;

public class Transporte {
    private static final String NOMBRE_CAPA = "TRANSPORTE";
    private static final int PUERTO_ORIGEN = 35468;
    private static final int PUERTO_DESTINO = 35332;

    public PDU encapsular(PDU pduSesion) {
        String cabecera = String.format("[%s|pOrigen=%d|pDestino=%d|ControlError=Activo]", 
                                        NOMBRE_CAPA, PUERTO_ORIGEN, PUERTO_DESTINO);
        PDU pduTransporte = new PDU(cabecera, pduSesion.getCompleto());
        
        System.out.println("ENVIO DESDE " + NOMBRE_CAPA + " (Capa 4):");
        System.out.println("Agregando puertos y control de errores...");
        System.out.println("Cabecera: " + cabecera);
        System.out.println("PDU Transporte: " + pduTransporte.getCompleto() + "\n");
        
        return pduTransporte;
    }

    public PDU desencapsular(List<PDU> fragmentosRed) {
        System.out.println("RECEPCION DESDE " + NOMBRE_CAPA + " (Capa 4):");
        System.out.println("Reensamblando " + fragmentosRed.size() + " fragmentos...");
        
        // Reensamblar los datos
        StringBuilder datosReensamblados = new StringBuilder();
        for (PDU fragmento : fragmentosRed) {
            datosReensamblados.append(fragmento.getDatos());
        }
        
        String datosCompletos = datosReensamblados.toString();
        
        // Los datos reensamblados empiezan con [TRANSPORTE|...]
        // Buscar el fin de la cabecera de TRANSPORTE
        int finCabeceraTransporte = datosCompletos.indexOf("]");
        if (finCabeceraTransporte == -1) {
            throw new RuntimeException("Error: Formato de datos incorrecto");
        }
        
        String cabeceraTCP = datosCompletos.substring(0, finCabeceraTransporte + 1);
        String datosSesion = datosCompletos.substring(finCabeceraTransporte + 2); // +2 para saltar "]|"
        
        PDU pduSesion = new PDU("", datosSesion);
        
        System.out.println("Verificando control de errores y puertos...");
        System.out.println("Cabecera TCP recuperada: " + cabeceraTCP);
        System.out.println("Datos reensamblados correctamente.\n");
        
        return pduSesion;
    }
}