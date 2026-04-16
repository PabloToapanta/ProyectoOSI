package Capas;
import PDU.*;
import java.util.Base64;

/**
 * Implementación de la Capa 6 (Presentación) del modelo OSI.
 * Su responsabilidad principal es la traducción, formateo y sintaxis de los datos.
 * En esta simulación, asegura la independencia de la plataforma codificando
 * la carga útil (payload) mediante el estándar Base64, simulando el proceso
 * de serialización y cifrado de datos antes de su transmisión en la red.
 */
public class Presentacion extends CapaOSI {
    public Presentacion(){
        super("PRESENTACION");
    }

    /**
     * Proceso de transformación y codificación (Flujo descendente).
     * @param pduAplicacion Objeto PDU con la información en texto plano.
     * @return PDU (Datos) conteniendo el mensaje codificado y la cabecera de formato.
     */
    public Datos encapsular(Datos pduAplicacion) {
        String datosAplicacion = pduAplicacion.getCompleto();

        // Transformación del formato de los datos. Se utiliza Base64 para 
        // representar el proceso en el que la capa de presentación prepara 
        // la información para un tránsito seguro y estandarizado.
        String datosCodificados = Base64.getEncoder().encodeToString(datosAplicacion.getBytes());
        
        String cabecera = String.format("[%s|formato=Base64]", NOMBRE_CAPA);
        Datos pduPresentacion = new Datos(cabecera, datosCodificados);
        
        return pduPresentacion;
    }

    /**
     * Proceso de decodificación y restauración (Flujo ascendente).
     * @param pduPresentacion Objeto PDU cifrado recibido desde la capa de sesión.
     * @return PDU (Datos) con la carga útil restaurada a su formato original.
     */
    public Datos desencapsular(Datos pduPresentacion) {
        // Obtenemos los datos que incluyen la cabecera de presentación
        String datosCompletos = pduPresentacion.getDatos();
        // Buscamos dónde termina la cabecera de PRESENTACION
        int finCabecera = datosCompletos.indexOf("]");
        if (finCabecera == -1) {
            throw new RuntimeException("Error: No se encontró cabecera de PRESENTACION");
        }
        // Aislamiento de la carga útil codificada (se omiten los caracteres de separación "]|").
        String datosCodificados = datosCompletos.substring(finCabecera + 2);
        // Reversión de la transformación: se decodifica el string Base64 
        // devolviéndolo a un formato comprensible para la Capa de Aplicación.
        String datosDecodificados = new String(Base64.getDecoder().decode(datosCodificados));
        
        return new Datos("", datosDecodificados);
    }
}