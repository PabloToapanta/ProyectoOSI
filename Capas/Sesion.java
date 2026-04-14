package Capas;

import PDU.*;

public class Sesion {
    private static final String NOMBRE_CAPA = "SESION";

    public PDU encapsular(PDU pduPresentacion, String hostOrigen, String hostDestino) {
        String cabecera = String.format("[%s|origen=%s|destino=%s]", 
                                        NOMBRE_CAPA, hostOrigen, hostDestino);
        PDU pduSesion = new PDU(cabecera, pduPresentacion.getCompleto());
        
        System.out.println("ENVIO DESDE " + NOMBRE_CAPA + " (Capa 5): Sesión establecida entre " + hostOrigen + " y " + hostDestino);
        System.out.println("PDU Sesion: " + pduSesion.getCompleto() + "\n");
        return pduSesion;
    }

    public PDU desencapsular(PDU pduSesion, String nombreHostActual) {
        // Los datos de sesión vienen con formato: [SESION|origen=...|destino=...]|[PRESENTACION|...]|...
        String datosCompletos = pduSesion.getDatos();
        
        // Extraer la cabecera de SESION
        int finCabecera = datosCompletos.indexOf("]");
        if (finCabecera == -1) {
            throw new RuntimeException("Error: No se encontró cabecera de SESION");
        }
        
        String cabecera = datosCompletos.substring(0, finCabecera + 1);
        String destinoEsperado = "";
        
        // Extraer el destino de la cabecera
        String[] partes = cabecera.split("\\|");
        for(String parte : partes) {
            if(parte.startsWith("destino=")) {
                destinoEsperado = parte.substring(8);
                // Quitar el ']' del final si existe
                if (destinoEsperado.endsWith("]")) {
                    destinoEsperado = destinoEsperado.substring(0, destinoEsperado.length() - 1);
                }
            }
        }

        System.out.println("RECEPCION DESDE " + NOMBRE_CAPA + " (Capa 5):");
        System.out.println("Validando destinatario: Esperado=" + destinoEsperado + ", Actual=" + nombreHostActual);

        if (!destinoEsperado.equals(nombreHostActual)) {
            throw new RuntimeException("ERROR DE SESIÓN: Este paquete estaba dirigido a '" + 
                                       destinoEsperado + "' pero fue interceptado por '" + nombreHostActual + "'.");
        }

        System.out.println("Sesión validada correctamente. El receptor autorizado ha recibido los datos.\n");
        
        // Pasar los datos sin la cabecera de SESION
        String datosParaPresentacion = datosCompletos.substring(finCabecera + 2);
        return new PDU("", datosParaPresentacion);
    }
}