import Hosts.*;
import java.util.*;

public class Menu {

    private Scanner scanner;
    private List<Host> hosts;
    private Host hostActual;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.hosts = new ArrayList<>();
        inicializarHosts();
    }

    // Dentro de Menu.java
    private void inicializarHosts() {
        // Nombre, IP, MAC, Puerto
        hosts.add(new Host("PC-Juan", "192.168.0.10", "AA:BB:CC:DD:EE:01", 35468));
        hosts.add(new Host("PC-Maria", "192.168.0.12", "AA:BB:CC:DD:EE:02", 35332));
        hosts.add(new Host("PC-Intruso", "192.168.0.99", "FF:FF:FF:EE:DD:CC", 40000));
    }

    public void iniciar() {
        boolean continuar = true;

        System.out.println("===================================================");
        System.out.println("   SIMULACION DEL MODELO OSI DE 7 CAPAS");
        System.out.println("===================================================");

        seleccionarHost();

        while (continuar) {
            mostrarMenu();
            int opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    enviarMensaje();
                    break;
                case 2:
                    recibirMensaje();
                    break;
                case 3:
                    seleccionarHost();
                    break;
                case 4:
                    continuar = false;
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.\n");
            }
        }

        scanner.close();
    }

    private void seleccionarHost() {
        System.out.println("\n--- SELECCION DE HOST ---");
        System.out.println("Hosts disponibles:");
        for (int i = 0; i < hosts.size(); i++) {
            System.out.println((i + 1) + ". " + hosts.get(i).getNombre());
        }
        System.out.print("Seleccione un host (1-" + hosts.size() + "): ");

        try {
            int seleccion = Integer.parseInt(scanner.nextLine().trim());
            if (seleccion >= 1 && seleccion <= hosts.size()) {
                hostActual = hosts.get(seleccion - 1);
                System.out.println("Host actual: " + hostActual.getNombre() + "\n");
            } else {
                hostActual = hosts.get(0);
                System.out.println("Seleccion invalida. Usando " + hostActual.getNombre() + "\n");
            }
        } catch (NumberFormatException e) {
            hostActual = hosts.get(0);
            System.out.println("Entrada invalida. Usando " + hostActual.getNombre() + "\n");
        }
    }

    private Host seleccionarDestinatario() {
        System.out.println("\n--- SELECCION DE DESTINATARIO ---");
        System.out.println("Destinatarios disponibles:");

        List<Host> disponibles = new ArrayList<>();
        for (Host h : hosts) {
            if (!h.getNombre().equals(hostActual.getNombre())) {
                disponibles.add(h);
            }
        }

        for (int i = 0; i < disponibles.size(); i++) {
            System.out.println((i + 1) + ". " + disponibles.get(i).getNombre());
        }
        System.out.print("Seleccione destinatario (1-" + disponibles.size() + "): ");

        try {
            int seleccion = Integer.parseInt(scanner.nextLine().trim());
            if (seleccion >= 1 && seleccion <= disponibles.size()) {
                return disponibles.get(seleccion - 1);
            }
        } catch (NumberFormatException e) {
            // Error, retorna el primero
        }

        return disponibles.get(0);
    }

    private void mostrarMenu() {
        System.out.println("===================================================");
        System.out.println("   HOST ACTUAL: " + hostActual.getNombre());
        System.out.println("===================================================");
        System.out.println("1. Enviar mensaje");
        System.out.println("2. Recibir mensaje");
        System.out.println("3. Cambiar de host");
        System.out.println("4. Salir");
        System.out.println("===================================================");
        System.out.print("Seleccione una opcion: ");
    }

    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void enviarMensaje() {
        System.out.println("\n===== ENVIAR MENSAJE =====");
        System.out.print("Ingrese el mensaje a enviar: ");
        String mensaje = scanner.nextLine().trim();

        if (mensaje.isEmpty()) {
            System.out.println("Error: El mensaje no puede estar vacio.\n");
            return;
        }

        if (mensaje.length() > 500) {
            System.out.println("Error: El mensaje excede 500 bytes.\n");
            return;
        }

        Host destinatario = seleccionarDestinatario();

        try {
            System.out.println("\nEnviando mensaje desde " + hostActual.getNombre() +
                    " hacia " + destinatario.getNombre() + "...");
            hostActual.enviarDatos(mensaje, destinatario);

        } catch (Exception e) {
            System.out.println("Error durante el envio: " + e.getMessage() + "\n");
        }
    }

    private void recibirMensaje() {
        System.out.println("\n===== RECIBIR MENSAJE =====");
        System.out.println(hostActual.getNombre() + " intenta leer datos del cable...");

        try {
            hostActual.recibirDatos();

        } catch (Exception e) {
            System.out.println("Error durante la recepcion: " + e.getMessage() + "\n");
        }
    }
}