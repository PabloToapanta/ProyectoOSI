package Hosts;

import PDU.*;
import Capas.*;
import java.util.*;
import Utilidades.*;

/**
 * Clase que representa una computadora o dispositivo en la simulación.
 * Actúa como un "Director" u "Orquestador": su trabajo no es procesar los
 * datos,
 * sino coordinar el orden en el que trabajan las 7 capas y delegar la impresión
 * de resultados al Visualizador, manteniendo una arquitectura limpia.
 */
public class Host {
    private String nombre;
    private String ip;
    private String mac;
    private int puerto;

    // Composición: El Host "tiene" una instancia de cada capa del modelo OSI.
    private Aplicacion capa7 = new Aplicacion();
    private Presentacion capa6 = new Presentacion();
    private Sesion capa5 = new Sesion();
    private Transporte capa4 = new Transporte();
    private Red capa3 = new Red();
    private Enlace capa2 = new Enlace();
    private Fisica capa1 = new Fisica();

    /**
     * Constructor del Host. Inicializa la configuración de red de la máquina.
     */
    public Host(String nombre, String ip, String mac, int puerto) {
        this.nombre = nombre;
        this.ip = ip;
        this.mac = mac;
        this.puerto = puerto;
    }

    // Getters

    public String getNombre() {
        return nombre;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public int getPuerto() {
        return puerto;
    }

    /**
     * Proceso de emisión de datos (Flujo de bajada: Capas 7 a 1).
     * Aplica el principio de Separación de Responsabilidades: las capas solo hacen
     * cálculos y devuelven objetos, mientras que este método recoge esos objetos
     * y se los pasa al VisualizadorOSI para dibujarlos en consola.
     */
    public void enviarDatos(String mensajeUsuario, Host destinatario) {
        System.out.println("\n--- " + nombre + " INICIA ENVÍO ---");

        // CAPA 7: APLICACIÓN
        Datos p7 = capa7.encapsular(mensajeUsuario);
        VisualizadorOSI.imprimirPanel("7 - APLICACION", "Envío",
                "Generación del mensaje y verificación de longitud.",
                mensajeUsuario, p7.getCabecera(), p7.getCompleto());

        // CAPA 6: PRESENTACIÓN
        Datos p6 = capa6.encapsular(p7);
        VisualizadorOSI.imprimirPanel("6 - PRESENTACION", "Envío",
                "Codificación de los datos a Base64.",
                p7.getCompleto(), p6.getCabecera(), p6.getCompleto());

        // CAPA 5: SESIÓN
        Datos p5 = capa5.encapsular(p6, this.getNombre(), destinatario.getNombre());
        VisualizadorOSI.imprimirPanel("5 - SESION", "Envío",
                "Establecimiento de sesión lógica entre hosts.",
                p6.getCompleto(), p5.getCabecera(), p5.getCompleto());

        // CAPA 4: TRANSPORTE
        Segmento p4 = capa4.encapsular(p5, this.getPuerto(), destinatario.getPuerto());
        VisualizadorOSI.imprimirPanel("4 - TRANSPORTE", "Envío",
                "Asignación de puertos de origen y destino.",
                p5.getCompleto(), p4.getCabecera(), p4.getCompleto());

        // CAPA 3: RED
        List<Paquete> p3 = capa3.encapsular(p4, this.getIp(), destinatario.getIp());

        // Uso de StringBuilder para concatenar múltiples elementos
        StringBuilder cabecerasRed = new StringBuilder();
        StringBuilder paquetesRed = new StringBuilder();
        for (Paquete pq : p3) {
            cabecerasRed.append(pq.getCabecera()).append("\n");
            paquetesRed.append(pq.getCompleto()).append("\n");
        }

        VisualizadorOSI.imprimirPanel("3 - RED", "Envío",
                "Fragmentación en " + p3.size() + " paquetes",
                p4.getCompleto(),
                cabecerasRed.toString().trim(), // Muestra TODAS las cabeceras
                paquetesRed.toString().trim()); // Muestra TODOS los paquetes

        // CAPA 2: ENLACE
        List<Trama> p2 = capa2.encapsular(p3, this.getMac(), destinatario.getMac());

        // Recopilar todas las tramas para mostrarlas
        StringBuilder cabecerasEnlace = new StringBuilder();
        StringBuilder tramasEnlace = new StringBuilder();
        for (Trama tr : p2) {
            cabecerasEnlace.append(tr.getCabecera()).append("\n");
            tramasEnlace.append(tr.getCompleto()).append("\n");
        }

        VisualizadorOSI.imprimirPanel("2 - ENLACE", "Envío",
                "Direccionamiento MAC y cálculo de código de integridad.",
                paquetesRed.toString().trim(), // La entrada son los paquetes de arriba
                cabecerasEnlace.toString().trim(),
                tramasEnlace.toString().trim());

        // CAPA 1: FÍSICA
        String nombreArchivo = capa1.encapsular(p2);
        VisualizadorOSI.imprimirPanel("1 - FISICA", "Envío",
                "Serialización de bits al medio físico (cable).",
                "Lista de " + p2.size() + " Tramas", "", "Archivo: " + nombreArchivo);

        System.out.println("--- DATOS ENVIADOS AL CABLE EXITOSAMENTE ---\n");
    }

    /**
     * Proceso de recepción de datos (Flujo de subida: Capas 1 a 7).
     * Funciona desenvolviendo progresivamente las capas de datos.
     * Incorpora un bloque Try-Catch global para detener el proceso si alguna
     * capa inferior detecta un fallo de seguridad o integridad estructural.
     */
    public void recibirDatos() {
        System.out.println("\n--- " + nombre + " INICIA RECEPCIÓN ---");
        try {
            // CAPA 1: FÍSICA
            List<Trama> p1 = capa1.desencapsular();

            StringBuilder tramasFisica = new StringBuilder();
            for (Trama tr : p1) {
                tramasFisica.append(tr.getCompleto()).append("\n");
            }

            
            VisualizadorOSI.imprimirPanel("1 - FISICA", "Recepción",
                    "Lectura de bits (deserialización) desde el medio físico (cable.bin).",
                    "Archivo: cable.bin", "Formato Binario", tramasFisica.toString().trim());

            // CAPA 2: ENLACE
            List<Paquete> p2 = capa2.desencapsular(p1);

            StringBuilder paquetesEnlace = new StringBuilder();
            for (Paquete pq : p2) {
                paquetesEnlace.append(pq.getCompleto()).append("\n");
            }

            VisualizadorOSI.imprimirPanel("2 - ENLACE", "Recepción",
                    "Verificación de integridad matemática y eliminacion de cabecera MAC.",
                    tramasFisica.toString().trim(),
                    p1.get(0).getCabecera() + " (Cabeceras MAC)",
                    paquetesEnlace.toString().trim());

            // CAPA 3: RED
            List<Segmento> p3 = capa3.desencapsular(p2);

            StringBuilder segmentosRed = new StringBuilder();
            for (Segmento seg : p3) {
                segmentosRed.append(seg.getCompleto()).append("\n");
            }

            //Extraemos la cabecera IP leyendo hasta el primer corchete "]"
            String cabeceraRed = p2.get(0).getDatos().substring(0, p2.get(0).getDatos().indexOf("]") + 1);

            VisualizadorOSI.imprimirPanel("3 - RED", "Recepción",
                    "Eliminacion de cabeceras IP ",
                    paquetesEnlace.toString().trim(),
                    cabeceraRed + " (Cabeceras IP)",
                    segmentosRed.toString().trim());

            // CAPA 4: TRANSPORTE
            Datos p4 = capa4.desencapsular(p3);

            // xtraemos la cabecera Transporte
            String cabeceraTransporte = p3.get(0).getDatos().substring(0, p3.get(0).getDatos().indexOf("]") + 1);

            VisualizadorOSI.imprimirPanel("4 - TRANSPORTE", "Recepción",
                    "Reensamblado de los " + p3.size() + " fragmentos ",
                    segmentosRed.toString().trim(),
                    cabeceraTransporte + " (Cabecera Transporte)",
                    p4.getCompleto());

            // CAPA 5: SESIÓN
            Datos p5 = capa5.desencapsular(p4, this.getNombre());

            // Extraemos la cabecera de Sesión
            String cabeceraSesion = p4.getCompleto().substring(0, p4.getCompleto().indexOf("]") + 1);

            VisualizadorOSI.imprimirPanel("5 - SESION", "Recepción",
                    "Validación del destinatario para mantener la sesión lógica autorizada.",
                    p4.getCompleto(),
                    cabeceraSesion,
                    p5.getCompleto());

            // CAPA 6: PRESENTACIÓN
            Datos p6 = capa6.desencapsular(p5);

            // Extraemos la cabecera de Presentación
            String cabeceraPresentacion = p5.getCompleto().substring(0, p5.getCompleto().indexOf("]") + 1);

            VisualizadorOSI.imprimirPanel("6 - PRESENTACION", "Recepción",
                    "Decodificación de los datos (de Base64 a texto plano).",
                    p5.getCompleto(),
                    cabeceraPresentacion,
                    p6.getCompleto());

            // CAPA 7: APLICACIÓN
            String p7 = capa7.desencapsular(p6);

            // Extraemos la cabecera de Aplicación
            String cabeceraAplicacion = p6.getCompleto().substring(0, p6.getCompleto().indexOf("]") + 1);

            VisualizadorOSI.imprimirPanel("7 - APLICACION", "Recepción",
                    "Extracción del mensaje final ",
                    p6.getCompleto(),
                    cabeceraAplicacion,
                    p7);

            System.out.println("========================================================================");
            System.out.println(" MENSAJE ENTREGADO CON ÉXITO A: " + nombre);
            System.out.println(" CONTENIDO: " + p7);
            System.out.println("========================================================================\n");

        } catch (RuntimeException e) {
            System.out.println("\n========================================================================");
            System.out.println(" ERROR DETECTADO");
            System.out.println("------------------------------------------------------------------------");
            System.out.println(" DETALLE: " + e.getMessage());
            System.out.println("========================================================================\n");
        }
    }
}