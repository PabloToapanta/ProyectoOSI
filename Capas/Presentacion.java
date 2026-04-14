package Capas;
import PDU.*;
import java.util.Base64;

public class Presentacion {
    private static final String NOMBRE_CAPA = "PRESENTACION";

    public PDU encapsular(PDU pduAplicacion) {
        String datosAplicacion = pduAplicacion.getCompleto();
        String datosCodificados = Base64.getEncoder().encodeToString(datosAplicacion.getBytes());
        
        String cabecera = String.format("[%s|formato=Base64]", NOMBRE_CAPA);
        PDU pduPresentacion = new PDU(cabecera, datosCodificados);

        System.out.printf("ENVIO DESDE PRESENTACION (Capa 6):\nDatos originales: %s\nTamaño original: %d bytes\nCodificación: Base64\n" +
                         "Datos codificados: %s\nTamaño codificado: %d bytes\n\n",
            datosAplicacion, datosAplicacion.getBytes().length, datosCodificados, datosCodificados.length());
        
        return pduPresentacion;
    }

    public PDU desencapsular(PDU pduPresentacion) {
        // Obtenemos los datos que incluyen la cabecera de presentación
        String datosCompletos = pduPresentacion.getDatos();
        
        // Buscamos dónde termina la cabecera de PRESENTACION
        int finCabecera = datosCompletos.indexOf("]");
        if (finCabecera == -1) {
            throw new RuntimeException("Error: No se encontró cabecera de PRESENTACION");
        }
        
        // Extraemos solo los datos en Base64, saltando el "]|" (por eso sumamos 2)
        String datosCodificados = datosCompletos.substring(finCabecera + 2);
        
        // Ahora sí decodificamos solo los datos puros
        String datosDecodificados = new String(Base64.getDecoder().decode(datosCodificados));

        System.out.printf("RECEPCION DESDE PRESENTACION (Capa 6):\nDatos codificados recibidos: %s\n" +
                         "Decodificación: Base64\nDatos decodificados: %s\n" +
                         "Tamaño decodificado: %d bytes\n\n",
            datosCodificados, datosDecodificados, datosDecodificados.getBytes().length);
        
        return new PDU("", datosDecodificados);
    }
}