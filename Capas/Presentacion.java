package Capas;
import PDU.*;
import java.util.Base64;

public class Presentacion extends CapaOSI {
    public Presentacion(){
        super("PRESENTACION");
    }
    public Datos encapsular(Datos pduAplicacion) {
        String datosAplicacion = pduAplicacion.getCompleto();
        String datosCodificados = Base64.getEncoder().encodeToString(datosAplicacion.getBytes());
        
        String cabecera = String.format("[%s|formato=Base64]", NOMBRE_CAPA);
        Datos pduPresentacion = new Datos(cabecera, datosCodificados);
        
        return pduPresentacion;
    }
    public Datos desencapsular(Datos pduPresentacion) {
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
        
        return new Datos("", datosDecodificados);
    }
}