package Hosts;

import PDU.*;
import Capas.*;
import java.util.*;


public class Host {
    private String nombre;
    private Aplicacion capa7 = new Aplicacion();
    private Presentacion capa6 = new Presentacion();
    private Sesion capa5 = new Sesion();
    private Transporte capa4 = new Transporte();
    private Red capa3 = new Red();
    private Enlace capa2 = new Enlace();
    private Fisica capa1 = new Fisica();

    public Host(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void enviarDatos(String mensajeUsuario, String nombreDestinatario) {
        System.out.println("\n--- " + nombre + " INICIA ENVÍO ---");
        PDU p7 = capa7.encapsular(mensajeUsuario);
        PDU p6 = capa6.encapsular(p7);
        PDU p5 = capa5.encapsular(p6, this.nombre, nombreDestinatario);
        PDU p4 = capa4.encapsular(p5);
        List<PDU> p3 = capa3.encapsular(p4);
        List<PDU> p2 = capa2.encapsular(p3);
        capa1.encapsular(p2);
        System.out.println("--- DATOS ENVIADOS AL CABLE ---\n");
    }

    public void recibirDatos() {
        System.out.println("\n--- " + nombre + " INICIA RECEPCIÓN ---");
        try {
            List<PDU> p1 = capa1.desencapsular();
            List<PDU> p2 = capa2.desencapsular(p1);
            List<PDU> p3 = capa3.desencapsular(p2);
            PDU p4 = capa4.desencapsular(p3);
            PDU p5 = capa5.desencapsular(p4, this.nombre);
            PDU p6 = capa6.desencapsular(p5);
            String p7 = capa7.desencapsular(p6);
            
            System.out.println("--- MENSAJE RECIBIDO POR " + nombre + " ---");
            System.out.println("Contenido: " + p7 + "\n");
            
        } catch (RuntimeException e) {
            System.out.println("LA RECEPCIÓN FALLÓ: " + e.getMessage() + "\n");
        }
    }
}