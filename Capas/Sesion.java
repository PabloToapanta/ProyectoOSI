package Capas;

import PDU.*;

public class Sesion extends CapaOSI {
    public Sesion(){
        super("SESION");
    }

    public Datos encapsular(Datos pduPresentacion, String hostOrigen, String hostDestino) {
        String cabecera = String.format("[%s|origen=%s|destino=%s]", 
                                        NOMBRE_CAPA, hostOrigen, hostDestino);
        Datos pduSesion = new Datos(cabecera, pduPresentacion.getCompleto());
        
        return pduSesion;
    }

    public Datos desencapsular(Datos pduSesion, String nombreHostActual) {
  
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


        if (!destinoEsperado.equals(nombreHostActual)) {
            throw new RuntimeException("ERROR DE SESIÓN: Este paquete estaba dirigido a '" + 
                                       destinoEsperado + "' pero fue interceptado por '" + nombreHostActual + "'.");
        }


        // Pasar los datos sin la cabecera de SESION
        String datosParaPresentacion = datosCompletos.substring(finCabecera + 2);
        return new Datos("", datosParaPresentacion);
    }
}