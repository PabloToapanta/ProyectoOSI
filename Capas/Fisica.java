package Capas;

import PDU.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Fisica extends CapaOSI {

    public Fisica(){
        super("FISICA");
    }
    private static final String ARCHIVO_CABLE = "cable.bin";
    /**
     * Encapsula serializando PDUs a un archivo binario.
     */
    public String encapsular(List<Trama> tramas) {


        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CABLE))) {
            salida.writeObject(tramas);
            salida.flush();
            
            for (int i = 0; i < tramas.size(); i++) {

            }
            
            return ARCHIVO_CABLE;
            
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir archivo binario: " + e.getMessage());
        }
    }

    /**
     * Desencapsula deserializando PDUs desde el archivo binario.
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