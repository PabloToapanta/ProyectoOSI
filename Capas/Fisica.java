package Capas;

import PDU.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementación de la Capa 1 (Física) del modelo OSI.
 * Se encarga de la transmisión y recepción de la secuencia de bits sin procesar 
 * a través de un medio de transmisión físico. En esta simulación, el medio 
 * guiado (cable de cobre/fibra) se representa mediante un archivo binario.
 */
public class Fisica extends CapaOSI {

    public Fisica(){
        super("FISICA");
    }
    private static final String ARCHIVO_CABLE = "cable.bin";
    /**
     * Proceso de transmisión al medio físico (Flujo descendente).
     * Convierte las estructuras lógicas (Tramas) en un flujo continuo de bytes (Serialización).
     * Nota Arquitectónica: Al abrir el FileOutputStream sin el modo "append", 
     * * @param tramas Lista de PDUs de la capa de enlace a transmitir.
     * @return El identificador/ruta del medio utilizado.
     */
    public String encapsular(List<Trama> tramas) {


        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CABLE))) {
            salida.writeObject(tramas);
            // Asegura que todos los bytes en el buffer sean escritos físicamente en el disco.
            salida.flush();
            salida.flush();
            
            for (int i = 0; i < tramas.size(); i++) {

            }
            
            return ARCHIVO_CABLE;
            
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir archivo binario: " + e.getMessage());
        }
    }

    
    /**
     * Proceso de recepción desde el medio físico (Flujo ascendente).
     * Lee la señal del medio compartido y reconstruye las estructuras de datos (Deserialización)
     * para que puedan ser procesadas por la tarjeta de red (Capa 2).
     * * @return Lista de Tramas recuperadas del flujo de bytes.
     */
    public List<Trama> desencapsular() {

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ARCHIVO_CABLE))) {
            List<Trama> tramasRecibidas = (List<Trama>) entrada.readObject();
            
            return tramasRecibidas;
            
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error al leer archivo binario: " + e.getMessage());
        }
    }
}