package envejecimientoSimulacionP2;


//AÑADIDOOOO

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;

public class Parte2intento {
    private int cantidadMarcos;
    private HashMap<Integer, Integer> tablaPaginas = new HashMap<>();
    private LinkedList<Integer> marcos = new LinkedList<>();
    private HashMap<Integer, Byte> edadesMarcos = new HashMap<>();
    private List<Referencia> referencias = new ArrayList<>();
    private int fallosPagina = 0;

    public Parte2intento(int cantidadMarcos, String nombreArchivoReferencias) throws IOException {
        this.cantidadMarcos = cantidadMarcos;
        cargarArchivo(nombreArchivoReferencias);

        // Hilos para simular los dos procesos (Actualización - Aging)
        // Se simplificó la creación de una clase separada para cada uno y se usó el operador colon para crear un objeto Runnable sobre los métodos que se ejecutarán en cada hilo.
        Thread hiloActualizacion = new Thread(this::actualizarPaginasYMarcos);
        Thread hiloAging = new Thread(this::algoritmoAging);

        hiloActualizacion.start();
        hiloAging.start();
        long startTime = System.currentTimeMillis();

        // Ejecución del método join sobre los 2 Threads para esperar a que acaben
        try {
            hiloActualizacion.join();
            hiloAging.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Medición del tiempo que toma el programa en calcular los fallos de página según las especificaciones dads.
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        // Luego de que terminan, se imprime el resultado final.
        System.out.println("\n>>En Total, se generaron " + fallosPagina +  " fallos de página.");
        System.out.println("Tiempo total de ejecución: " + totalTime + " milisegundos.");

    }

    // Para cumplir con la especificación (1) del modo 2, se simula la carga de referencias una por una.
    private void cargarArchivo(String nombreArchivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.startsWith("TP") && !linea.startsWith("NF") && !linea.startsWith("NC") && !linea.startsWith("NF_NC_Filtro") && !linea.startsWith("NR")&& !linea.startsWith("NP")) {
                    String[] partes = linea.split(",");
                    int paginaVirtual = Integer.parseInt(partes[1]);
                    int desplazamiento = Integer.parseInt(partes[2]);
                    referencias.add(new Referencia(paginaVirtual, desplazamiento));
                }
            }
        }
    }

    // Para cumplir con la especificación (2) del modo 2, se definieron los siguientes 3 métodos sincronizados:
    // 1.Actualizar páginas y marcos
    // 2.Obtener marco más antiguo
    // 3.Algoritmo Aging

    private synchronized void actualizarPaginasYMarcos() {
        for (Referencia ref : referencias) {
            int paginaVirtual = ref.getPaginaVirtual();
            if (!tablaPaginas.containsKey(paginaVirtual)) {
                fallosPagina++;
                System.out.println("\nFallo de página para página virtual " + paginaVirtual);
                
                // Esta parte se encarga de agregar un nuevo marco de memoria si aún no se han llenado todos los marcos.
                // Para ello, se agrega un nuevo marco a la lista de marcos y se agrega la página virtual a la tabla de páginas.
                if (marcos.size() < cantidadMarcos) {
                    int nuevoMarco = marcos.size();
                    marcos.add(nuevoMarco);
                    tablaPaginas.put(paginaVirtual, nuevoMarco);
                    // En esta linea, se inicializa la edad del marco en 0
                    edadesMarcos.put(nuevoMarco, (byte) 0); 
                } else {
                    int marcoReemplazar = obtenerMarcoMasAntiguo();
                    int paginaVirtualExpulsada = tablaPaginas.entrySet().stream()
                        .filter(entry -> marcoReemplazar == entry.getValue())
                        .map(Map.Entry::getKey)
                        .findFirst().orElse(-1);
                    
                    // Aqui, se elimina la página virtual expulsada de la tabla de páginas y se agrega la nueva página virtual.
                    if (paginaVirtualExpulsada != -1) {
                        tablaPaginas.remove(paginaVirtualExpulsada);
                    }
                    tablaPaginas.put(paginaVirtual, marcoReemplazar);
                    edadesMarcos.put(marcoReemplazar, (byte) 0); 
                    System.out.println("Página virtual " + paginaVirtualExpulsada + " expulsada del marco " + marcoReemplazar);
                }
            }
            System.out.println("Referencia a página virtual " + paginaVirtual + " mapeada al marco " + tablaPaginas.get(paginaVirtual));
        }
    }

    private synchronized int obtenerMarcoMasAntiguo() {
        int marcoMasAntiguo = -1;
        byte edadMaxima = -128; // empezar con el valor de byte más pequeño

        for (Map.Entry<Integer, Byte> entry : edadesMarcos.entrySet()) {
            if (entry.getValue() > edadMaxima) {
                marcoMasAntiguo = entry.getKey();
                edadMaxima = entry.getValue();
            }
        }

        return marcoMasAntiguo;
    }

    // El algoritmo Aging se encarga de actualizar las edades de los marcos de memoria y establecer el bit más significativo en 1 si se accedió a la página.
    // Esto, con el objetivo de que el método obtenerMarcoMasAntiguo pueda determinar cuál es el marco más antiguo.

    private synchronized void algoritmoAging() {
        // 1. Actualizar edades: Desplazar a la derecha la edad de cada marco
        for (Map.Entry<Integer, Byte> entry : edadesMarcos.entrySet()) {
            byte edadAntigua = entry.getValue();
            byte edadNueva = (byte) (edadAntigua >>> 1); // Desplazar a la derecha                
            edadesMarcos.put(entry.getKey(), edadNueva);
        }

        // 2. Comprobar referencias y establecer el bit más significativo si se accedió a la página
        for (Referencia ref : referencias) {
            int paginaVirtual = ref.getPaginaVirtual();
            if (tablaPaginas.containsKey(paginaVirtual)) {
                int marco = tablaPaginas.get(paginaVirtual);
                byte edadAntigua = edadesMarcos.get(marco);
                // Se define el bit más significativo como 1 para indicar que se accedió a la página
                // De esta forma, el método obtenerMarcoMasAntiguo puede determinar luego cuál es el marco más antiguo.
                byte edadNueva = (byte) (edadAntigua | 0b10000000);
                edadesMarcos.put(marco, edadNueva);
            }
        }
    }
}
