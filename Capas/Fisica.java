package Capas;

import PDU.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Fisica {
    private static final String ARCHIVO_CABLE = "cable.bin";

    /**
     * Encapsula serializando PDUs a un archivo binario.
     */
    public String encapsular(List<PDU> tramas) {
        System.out.printf("ENVIO DESDE FISICA (Capa 1):\nTransmitiendo %d tramas al archivo\n", 
            tramas.size());

        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CABLE))) {
            salida.writeObject(tramas);
            salida.flush();
            
            for (int i = 0; i < tramas.size(); i++) {
                System.out.printf("  Trama %d transmitida (tamaño: %d bytes)\n", 
                    i + 1, tramas.get(i).getCompleto().length());
            }
            
            System.out.printf("Archivo '%s' creado con éxito. Transmisión completada.\n\n", 
                ARCHIVO_CABLE);
            return ARCHIVO_CABLE;
            
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir archivo binario: " + e.getMessage());
        }
    }

    /**
     * Desencapsula deserializando PDUs desde el archivo binario.
     */
    public List<PDU> desencapsular() {
        System.out.printf("RECEPCION DESDE FISICA (Capa 1):\nLeyendo datos desde el cable ('%s')...\n", 
            ARCHIVO_CABLE);

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ARCHIVO_CABLE))) {
            List<PDU> tramasRecibidas = (List<PDU>) entrada.readObject();
            
            int contadorTramas = 0;
            for (PDU trama : tramasRecibidas) {
                contadorTramas++;
                System.out.printf("  Trama %d recibida (tamaño: %d bytes)\n", 
                    contadorTramas, trama.getCompleto().length());
            }

            System.out.printf("Total de tramas recibidas: %d\n\n", tramasRecibidas.size());
            return tramasRecibidas;
            
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error al leer archivo binario: " + e.getMessage());
        }
    }
}