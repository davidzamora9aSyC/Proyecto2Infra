package envejecimientoSimulacionP2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

public class Parte2 {

    private static String nombreArchivo;
    private List<Integer> paginas;  

    //private static TablaDePaginas tablaDePaginas;
    private static final Object lock = new Object();

    //Los fallos de página serán retornados

    private static Integer fallosPagina = 0;

    //Listas que indican la ubicación de las páginas en memoria real y virtual
    public static List<Integer> paginasMemoriaVirtual = Collections.synchronizedList(new ArrayList<Integer>());
    public static List<Integer> paginasEnUso = Collections.synchronizedList(new ArrayList<Integer>());
    public static List<Integer> paginasMemoriaReal = Collections.synchronizedList(new ArrayList<Integer>());

    
    public static Pagina[] paginasMemoriaVirtualJ;
    //public static int[] tablaDePaginas;
    public static int[] tablaAuxiliar;
    public static boolean[] marcosDePagina;

    //Tabla necesaria para ejecutar el algortimo de envejecimiento
    private static ConcurrentHashMap<Integer, Integer> envejecimiento = new ConcurrentHashMap<Integer, Integer>();

    private static int MP;
    private static int TP;
    private static int NF;
    private static int NC;
    private static int NF_NC_Filtro;
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

            if ((i+1)%4 == 0){      // El bit R se tiene que ejecutar cada 4 milisegundos, que es cada cuatro ejecuciones de estre Thread principal
               
                estructuraApoyo.pedirActualizar();
                
            }
            /**
            System.out.println(tablaDePaginas.toString());
            System.out.print("[");
            for(int k=0; k<tablaDePaginas.length; k++){
                System.out.print( tablaDePaginas[k] + ", ");
            }
            System.out.println("]");
            
            */

            if (Parte2.tablaDePaginas[numPagina] == -1) {
                Parte2.misses++;
                 
                int marcoVacio = 0;
                while (marcoVacio < Parte2.MP && Parte2.marcosDePagina[marcoVacio]) {
                    marcoVacio++;
                }
                

                if (marcoVacio < Parte2.MP) {                           //Se verifica si hay algun marco aun vacio
                    Parte2.marcosDePagina[marcoVacio] = true;           //Se deja el marco de pagina como ocupado
                    Parte2.tablaDePaginas[numPagina] = marcoVacio;      //Se actualiza la tabla de paginas, por lo que la pagina se agrega en el marco que estaba vacio
                    Parte2.estructuraApoyo.referenciarPagina(numPagina);

                } else {

                    int idEliminar = Apoyo.getIndexNRU();

                    Parte2.tablaDePaginas[numPagina] = Parte2.tablaDePaginas[idEliminar];
                    Parte2.tablaDePaginas[idEliminar] = -1;

                    Parte2.estructuraApoyo.referenciarPagina(numPagina);

                }
                
            } else {

                Parte2.hits++;
                Parte2.estructuraApoyo.referenciarPagina(numPagina);

            }
            
        }
        Parte2.estructuraApoyo.pedirActualizar();
        Parte2.actualizadorDeBits.detener();

        // TODO Se puede agregar en una funcion

        double porcentajeHits = ((double) hits / Parte2.NR) * 100.0;
        double porcentajeMisses = ((double) misses / Parte2.NR) * 100.0;

        System.out.println("Hits: " + hits + "  =  " + String.format("%.2f", porcentajeHits) + "%");
        System.out.println("Fallas: " + misses + "  =  " + String.format("%.2f", porcentajeMisses) + "%");
        System.out.println("Tiempo de ejecución: (hits * 30) ns + (misses * 10000000) ns = " + hits*30 + " + " + misses*10000000 + " = " + (hits*30 + misses*10000000) + " ns");
        System.out.println("Tiempo si todo fuera fallos: " + Parte2.NR*10000000 + " ns");
        System.out.println("Tiempo si todo fuera hits: " + Parte2.NR*30 + " ns");

    }

    

    public static void cargarListas() {
        Parte2.tablaAuxiliar = new int[Parte2.NP];
        Parte2.marcosDePagina = new boolean[Parte2.MP];
        Parte2.paginasMemoriaVirtualJ = new Pagina[Parte2.NP];
        Parte2.tablaDePaginas = new int[Parte2.NP];

        Parte2.estructuraApoyo = new Apoyo(Parte2.paginasMemoriaVirtualJ,  NP);
        Parte2.actualizadorDeBits = new ActualizadorDeBits(estructuraApoyo);

        for(int i=0; i<NP; i++){
            Parte2.paginasMemoriaVirtualJ[i] = new Pagina(i, TP/4);
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

        Parte2.TP = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        Parte2.NF = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        Parte2.NC = (int) Integer.parseInt(reader.readLine().split("=")[1]);
        Parte2.NF_NC_Filtro = (int) Integer.parseInt(reader.readLine().split("=")[1]);
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


    public static int getIndexNRU(){
        LinkedList<Integer> clase0 = new LinkedList<Integer>();
        LinkedList<Integer> clase1 = new LinkedList<Integer>();
        Random random = new Random();
        int randomIndex;

        for(Pagina pag: paginasMemoriaVirtualJ){
            if (pag.getR() == 1) {
                clase0.add(pag.getId());
            } else {
                clase1.add(pag.getId());
            }
        }

        if (clase0.size() >= 1) {
            randomIndex = random.nextInt(clase0.size());
        } else if (clase1.size() >= 1) {
            randomIndex = random.nextInt(clase1.size());
        } else {
            randomIndex = -1;
        }
        return randomIndex;    
    }

    
}