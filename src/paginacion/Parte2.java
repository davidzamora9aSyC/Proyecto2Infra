package paginacion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Parte2 {

    private static String nombreArchivo;
   
    public static Pagina[] paginasMemoriaVirtualJ;
    public static int[] tablaAuxiliar;
    public static boolean[] marcosDePagina;

    private static int MP;
    private static int  NR;
    private static int NP;
    private static String[] referencias;
    private static int[] tablaDePaginas;
    private static Apoyo estructuraApoyo;
    private static ActualizadorDeBits actualizadorDeBits;

    private static int hits = 0;
    private static int misses = 0;
    

    public Parte2(Integer MP, String nombreArchivo) throws IOException {

        Parte2.MP = MP;
        Parte2.nombreArchivo = nombreArchivo;
    }


    public void ejecutarModo2() throws IOException
    {   
        leerArchivo();

        String[] partes;
        int numPagina;
        int desplazamiento;

        Parte2.actualizadorDeBits.start();

        for (int i = 0; i < Parte2.NR; i++) {

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            partes = Parte2.referencias[i].split(",");

            numPagina = Integer.parseInt(partes[1]);
            desplazamiento = Integer.parseInt(partes[2]);


            if (Parte2.tablaDePaginas[numPagina] == -1) {
                Parte2.misses++;
                 
                int marcoVacio = 0;
                while (marcoVacio < Parte2.MP && Parte2.marcosDePagina[marcoVacio]) {
                    marcoVacio++;
                }
                

                if (marcoVacio < Parte2.MP) {                           //Se verifica si hay algun marco aun vacio
                    Parte2.marcosDePagina[marcoVacio] = true;           //Se deja el marco de pagina como ocupado
                    Parte2.tablaDePaginas[numPagina] = marcoVacio;      //Se actualiza la tabla de paginas, por lo que la pagina se agrega en el marco que estaba vacio
                    Apoyo.setearUnBit(numPagina, 1);

                } else {

                    int idEliminar = Apoyo.getIndexNRU();

                    Parte2.tablaDePaginas[numPagina] = Parte2.tablaDePaginas[idEliminar];
                    Parte2.tablaDePaginas[idEliminar] = -1;

                    Apoyo.setearUnBit(numPagina, 1);
                    Apoyo.setearUnBit(idEliminar, 0);


                }
                
            } else {

                Parte2.hits++;
                
            }
            
        }
        Parte2.actualizadorDeBits.detener();

        // TODO Se puede agregar en una funcion

        double porcentajeHits = ((double) hits / Parte2.NR) * 100.0;
        double porcentajeMisses = ((double) misses / Parte2.NR) * 100.0;

        System.out.println("Hits: " + hits + "  =  " + String.format("%.2f", porcentajeHits) + "%");
        System.out.println("Fallas: " + misses + "  =  " + String.format("%.2f", porcentajeMisses) + "%");
        System.out.println("Tiempo de ejecución: (hits * 30) ns + (misses * 10000000) ns = " + hits*30 + " + " + misses*10000000L + " = " + (hits*30 + misses*10000000L) + " ns");
        System.out.println("Tiempo si todo fuera fallos: " + Parte2.NR*10000000L + " ns");
        System.out.println("Tiempo si todo fuera hits: " + Parte2.NR*30 + " ns");

    }

    

    public static void cargarListas() {
        Parte2.tablaAuxiliar = new int[Parte2.NP];
        Parte2.marcosDePagina = new boolean[Parte2.MP];
        Parte2.paginasMemoriaVirtualJ = new Pagina[Parte2.NP];
        Parte2.tablaDePaginas = new int[Parte2.NP];

        Parte2.estructuraApoyo = new Apoyo(Parte2.paginasMemoriaVirtualJ,  NP, Parte2.marcosDePagina);
        Parte2.actualizadorDeBits = new ActualizadorDeBits(estructuraApoyo);

        for(int i=0; i<NP; i++){
            Parte2.paginasMemoriaVirtualJ[i] = new Pagina(i);
        }

        for (int i = 0; i < MP; i++) {
            Parte2.marcosDePagina[i] = false;
        }
    
        for (int i = 0; i < NP; i++) {
            Parte2.tablaDePaginas[i] = -1;
            Parte2.tablaAuxiliar[i] = i;
        }
        
    }

    
    public static synchronized void leerArchivo() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo));
        String linea;
        int sobrante;
        sobrante = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        sobrante = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        sobrante = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        sobrante = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        Parte2.NR = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        Parte2.NP = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        
        Parte2.referencias = new String[NR];
        int i = 0;

        while ((linea = reader.readLine()) != null) {
            Parte2.referencias[i] = linea;
            i++;
        }
        reader.close();

        cargarListas();

    }
    
}