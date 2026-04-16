import Hosts.*;
import java.util.*;

/**

 * Esta clase actúa como el punto de interacción entre el usuario y la simulación.
 * Se encarga de gestionar el estado de la aplicación (qué host estamos usando),
 * validar las entradas del usuario y delegar las acciones complejas a los objetos Host.
 */
public class Menu {

    // Herramienta para capturar la entrada del teclado
    private Scanner scanner;
    // Almacena todos los dispositivos registrados en nuestra simulación
    private List<Host> hosts;
    // Representa el estado actual: la computadora desde la cual el usuario está actuando
    private Host hostActual;

    /**
     * Constructor de la clase Menu.
     * Prepara el entorno de ejecución inicializando el lector de consola 
     * y cargando los datos de prueba al sistema.
     */
    public Menu() {
        this.scanner = new Scanner(System.in);
        this.hosts = new ArrayList<>();
        inicializarHosts();
    }

    /**
     * Método de configuración inicial (Setup).
     * Pre-carga la simulación con tres máquinas distintas, asignándoles 
     * sus propiedades de red simuladas (Nombre, IP, MAC y Puerto).
     */
    private void inicializarHosts() {
        // Nombre, IP, MAC, Puerto
        hosts.add(new Host("PC-Juan", "192.168.0.10", "AA:BB:CC:DD:EE:01", 35468));
        hosts.add(new Host("PC-Maria", "192.168.0.12", "AA:BB:CC:DD:EE:02", 35332));
        hosts.add(new Host("PC-Intruso", "192.168.0.99", "FF:FF:FF:EE:DD:CC", 40000));
    }

    /**
     * Bucle principal de la aplicación (Main Loop).
     * Mantiene el programa en ejecución constante hasta que el usuario decida salir.
     * Gestiona el flujo hacia las diferentes funcionalidades del menú.
     */
    public void iniciar() {
        boolean continuar = true;

        System.out.println("===================================================");
        System.out.println("   SIMULACION DEL MODELO OSI DE 7 CAPAS");
        System.out.println("===================================================");

        // Obligamos al usuario a elegir una máquina antes de empezar
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

    /**
     * Actualiza el estado del sistema cambiando el "hostActual".
     * Incluye manejo de excepciones para evitar que el programa se cierre 
     * si el usuario introduce letras en lugar de números.
     */
    private void seleccionarHost() {
        System.out.println("\n--- SELECCION DE HOST ---");
        System.out.println("Hosts disponibles:");
        for (int i = 0; i < hosts.size(); i++) {
            System.out.println((i + 1) + ". " + hosts.get(i).getNombre());
        }
        System.out.print("Seleccione un host (1-" + hosts.size() + "): ");

        try {
            // Leemos y limpiamos espacios en blanco
            int seleccion = Integer.parseInt(scanner.nextLine().trim());
            // Validación de límites para evitar un IndexOutOfBoundsException
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

    /**
     * Muestra una lista filtrada de máquinas a las que se les puede enviar datos.
     * Implementa lógica para evitar que el usuario se envíe un mensaje a sí mismo.
     * * @return El objeto Host seleccionado como receptor.
     */
    private Host seleccionarDestinatario() {
        System.out.println("\n--- SELECCION DE DESTINATARIO ---");
        System.out.println("Destinatarios disponibles:");

        // Creamos una lista temporal excluyendo al host actual
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

    /**
     * Imprime en consola las opciones disponibles del sistema.
     * Es puramente visual (Vista).
     */
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

    /**
     * Método auxiliar para capturar opciones del menú de forma segura.
     * * @return El número ingresado por el usuario, o -1 si la entrada es inválida.
     */

    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Controlador del flujo de envío.
     * Realiza validaciones primarias (como verificar que el mensaje no esté vacío o exceda
     * el límite de memoria permitido) antes de delegar el trabajo pesado al hostActual.
     */ 
    private void enviarMensaje() {
        System.out.println("\n===== ENVIAR MENSAJE =====");
        System.out.print("Ingrese el mensaje a enviar: ");
        String mensaje = scanner.nextLine().trim();

        // Validación 1: Evitar procesar mensajes vacíos
        if (mensaje.isEmpty()) {
            System.out.println("Error: El mensaje no puede estar vacio.\n");
            return;
        }

        if (mensaje.length() > 500) {
            System.out.println("Error: El mensaje excede 500 caracteres.\n");
            return;
        }

        Host destinatario = seleccionarDestinatario();

        try {
            
            System.out.println("\nEnviando mensaje desde " + hostActual.getNombre() +
                    " hacia " + destinatario.getNombre() + "...");

            // Delegación de la acción al modelo lógico (Host)
            hostActual.enviarDatos(mensaje, destinatario);

        } catch (Exception e) {
            System.out.println("Error durante el envio: " + e.getMessage() + "\n");
        }
    }

    /**
     * Controlador del flujo de recepción.
     * Instruye al host actual para que lea el medio físico e intente decodificar
     * la información. Cualquier error en el proceso se captura aquí para no romper la UI.
     */
    private void recibirMensaje() {
        System.out.println("\n===== RECIBIR MENSAJE =====");
        System.out.println(hostActual.getNombre() + " intenta leer datos del cable...");

        try {
            // Delegación de la acción al modelo lógico (Host)
            hostActual.recibirDatos();

        } catch (Exception e) {
            System.out.println("Error durante la recepcion: " + e.getMessage() + "\n");
        }
    }
}