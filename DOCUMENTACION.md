#  Simulador del Modelo OSI (POO en Java)

## Resumen del Proyecto
Este proyecto simula el viaje de un mensaje a través de las 7 capas del Modelo OSI (Aplicación a Física). El objetivo principal no es usar protocolos reales de red (como HTTP o TCP), sino **demostrar dominio de la Programación Orientada a Objetos (POO)** mediante encapsulamiento, herencia, polimorfismo y separación de responsabilidades.

---

## Arquitectura y Patrones (¡Importante para la Defensa!)
Para cumplir al 100% con la rúbrica, el proyecto se reestructuró con las siguientes buenas prácticas:

1. **Herencia y Abstracción (`CapaOSI.java`)**
   - **Por qué lo hicimos:** La rúbrica pedía una "abstracción común". En lugar de repetir código en las 7 capas, creamos la clase abstracta `CapaOSI`. Todas las capas (`Red`, `Enlace`, etc.) usan `extends Capa`. Esto nos permite heredar atributos (como el nombre de la capa) y estandarizar el programa.
2. **Objetos Intermedios (`Mensaje`, `Segmento`, `Paquete`, `Trama`)**
   - **Por qué lo hicimos:**  Estas clases heredan de la clase base `PDU`. Dependiendo de la capa en la que estemos, el dato se transforma en el objeto correspondiente (ej. Capa 4 crea `Segmento`, Capa 3 crea `Paquete`).
3. **Separación de Lógica y Vista (`VisualizadorOSI.java`)**
   - **Por qué lo hicimos:** Es una mala práctica (y penalizada en la rúbrica) poner `System.out.println` dentro de la lógica de negocio. Las clases de las capas ahora son **puramente lógicas** (solo reciben y devuelven datos). La impresión de esos datos se delegó completamente a la clase `VisualizadorOSI` ubicada en la carpeta `Utilidades`.
4. **Independencia del Host (Sin Datos Quemados)**
   - **Por qué lo hicimos:** Las capas no tienen la IP ni la MAC guardadas por defecto. Es la clase `Host` quien guarda la IP, MAC y Puerto de la máquina, y se los pasa a las capas en el momento del envío. Esto permite tener infinitos hosts distintos.

---

## Flujo de Ejecución (Cómo viajan los datos)
1. **Envío:** El `Host` toma un texto, llama a la Capa 7 (`encapsular`), recibe un objeto, y se lo pasa a la Capa 6. Así hasta la Capa 1, donde se guarda en `cable.bin`.
2. **Recepción:** El `Host` lee `cable.bin` llamando a la Capa 1 (`desencapsular`), y va subiendo las muñecas rusas (quitando cabeceras) hasta extraer el texto en la Capa 7.
3. **El Medio Compartido:** Si un host envía un mensaje y nadie lo lee, y luego otro host envía otro mensaje, **el primero se sobrescribe**. Esto es a propósito, ya que simula un cable físico real, no una base de datos.