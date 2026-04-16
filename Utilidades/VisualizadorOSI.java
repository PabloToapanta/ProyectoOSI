package Utilidades;


public class VisualizadorOSI {

    public static void imprimirPanel(String nombreCapa, String accion, String proceso, 
                                     String entrada, String metadatos, String salida) {
        System.out.println("\n========================================================================");
        System.out.println("  CAPA: " + nombreCapa.toUpperCase() + " (" + accion.toUpperCase() + ")");
        System.out.println("------------------------------------------------------------------------");
        System.out.println(" [>] PROCESO   : " + proceso);
        
        // Usamos un formato multilinea para que se vean todos los paquetes/tramas
        System.out.println("\n [>] ENTRADA   :\n" + indentar(entrada));
        System.out.println("\n [>] CABECERA :\n" + indentar(metadatos));
        System.out.println("\n [>] SALIDA    :\n" + indentar(salida));
        System.out.println("========================================================================");
    }

    // Este método toma un texto (aunque tenga muchas líneas) y le pone espacios a la izquierda
    // para que se vea ordenado dentro del panel.
    private static String indentar(String texto) {
        if (texto == null || texto.isEmpty()) return "      Ninguno";
        return texto.replaceAll("(?m)^", "      "); 
    }
}