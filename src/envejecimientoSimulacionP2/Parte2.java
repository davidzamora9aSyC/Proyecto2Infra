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


    public Parte2(Integer MP, String nombreArchivo) throws IOException {

        this.MP = MP;
        Parte2.nombreArchivo = nombreArchivo;
    }


    public void ejecutarModo2() throws IOException
    {   
        leerArchivo();
        cargarListas();

        String[] partes;
        int numPagina;
        int desplazamiento;

        int hits = 0;
        int misses = 0;
        //Actualizador actualizador = new Actualizador(this.conteo);
        //actualizador.start();

        for (int i = 0; i < Parte2.NR; i++) {

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            partes = Parte2.referencias[i].split(",");

            numPagina = Integer.parseInt(partes[1]);
            desplazamiento = Integer.parseInt(partes[2]);

            if ((i+1)%4 == 0){
                System.out.println(referencias[i]);
                //conteo.pedirActualizar();
            }
            
            
            if (Parte2.tablaDePaginas[i] == -1) {
                misses++;

                int marcoVacio = 0;
                for(int j=0; j< Parte2.MP; j++){
                    marcoVacio = j;
                    if (marcosDePagina[j]){
                        break;
                    }
                }

                if (marcoVacio < Parte2.MP) {                           //Se verifica si hay algun marco aun vacio
                    Parte2.marcosDePagina[marcoVacio] = true;           //Se deja el marco de pagina como ocupado
                    Parte2.tablaDePaginas[numPagina] = marcoVacio;      //Se actualiza la tabla de paginas, por lo que la pagina se agrega en el marco que estaba vacio

                    //this.conteo.referenciarPagina(numPagina);

                } else {

                    int idEliminar = getIndexNRU();
                    //int idx  = this.conteo.obtenerPaginaAEliminar(this.tablaPaginas);

                    Parte2.tablaDePaginas[numPagina] = Parte2.tablaDePaginas[idEliminar];
                    Parte2.tablaDePaginas[idEliminar] = -1;
                    //this.conteo.referenciarPagina(numPagina);
                }
                
            } else {
                hits++;
                //this.conteo.referenciarPagina(numPagina);
            }
            
        }
        //conteo.pedirActualizar();
        //actualizador.detener();
        //imprimirResultados(hits, misses);
    }

    
    public void cargarListas() {
        Parte2.tablaAuxiliar = new int[Parte2.NP];
        Parte2.marcosDePagina = new boolean[Parte2.MP];
        Parte2.paginasMemoriaVirtualJ = new Pagina[Parte2.NP];
        Parte2.tablaDePaginas = new int[Parte2.NP];

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

        Pagina[] listaPaginas = paginasMemoriaVirtualJ;

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



    public void cargaInicialPaginas()
    {
        // Recorre la lista de páginas y las carga en memoria
        for (int i = 0; i < paginas.size(); i++)
        {
            // Obtiene la página actual
            Integer paginaActual = paginas.get(i);
    
            // Sincroniza el acceso a la lista de páginas en memoria real y de páginas en uso
            synchronized(lock)
            {
                // Si la página actual ya está en memoria real pero no está en uso, la agrega a la lista de páginas en uso
                if(paginasMemoriaReal.contains(paginaActual) && !paginasEnUso.contains(paginaActual))
                {
                    paginasEnUso.add(paginaActual);
                }
                // Si la página actual no está en memoria real
                else if (!paginasMemoriaReal.contains(paginaActual))
                {
                    // Si hay espacio en memoria real, la agrega a la lista de páginas en memoria real y de páginas en uso
                    if (paginasMemoriaReal.size() < MP)
                    {
                        fallosPagina++;
                        // Si la página actual no está en uso, la agrega a la lista de páginas en uso
                        if (!paginasEnUso.contains(paginaActual))
                        {
                            paginasEnUso.add(paginaActual);
                        }

                        // Agrega la página actual a la lista de páginas en memoria real
                        paginasMemoriaReal.add(paginaActual);
                    }
                    // Si no hay espacio en memoria real, la agrega a la lista de páginas en memoria virtual
                    else if (!paginasMemoriaVirtual.contains(paginaActual))
                    {
                        paginasMemoriaVirtual.add(paginaActual);
                        fallosPagina++;
                    }

                    // Incrementa el contador de fallos de página
                    
                }
            }
        }
    }

    public static void updateTP() {
        int victima = -1; // Inicializa la página víctima con un valor imposible
        int valorEnvejecimiento = Integer.MAX_VALUE; // Inicializa el valor de envejecimiento con el máximo valor posible
        
        // Sincroniza el acceso a las estructuras de datos compartidas
        synchronized (lock) {
            while (paginasMemoriaVirtual.size() > 0) { // Mientras haya páginas en memoria virtual
                int paginaNueva = paginasMemoriaVirtual.remove(0); // Obtiene la primera página en memoria virtual y la elimina de la lista
                if (!paginasEnUso.contains(paginaNueva)) { // Si la página no está en uso
                    paginasEnUso.add(paginaNueva); // La marca como en uso
                }
                
                int posicionPaginaMenos = paginasMemoriaReal.indexOf(victima); // Obtiene la posición de la página víctima en la memoria real
                
                if (posicionPaginaMenos >= 0) {
                    paginasMemoriaReal.remove(posicionPaginaMenos); // Elimina la página víctima de la memoria real
                    envejecimiento.remove(victima); // Elimina la entrada correspondiente a la página víctima del mapa de envejecimiento
                }
                
                paginasMemoriaReal.add(paginaNueva); // Agrega la nueva página a la memoria real
                
                // Actualiza el mapa de envejecimiento
                for (Map.Entry<Integer, Integer> entry : envejecimiento.entrySet()) {
                    int paginaEnvejecimiento = entry.getKey();
                    int valorActual = entry.getValue();
                    
                    if (paginaEnvejecimiento == paginaNueva) {
                        valorActual = 0; // Si la página es nueva, su valor de envejecimiento es 0
                    } else {
                        valorActual = valorActual >> 1; // Desplaza los bits del valor de envejecimiento hacia la derecha
                        if (paginasMemoriaReal.contains(paginaEnvejecimiento)) {
                            valorActual = valorActual | (1 << 31); // Si la página está en memoria real, pone el bit más significativo a 1
                        }
                    }
                    
                    envejecimiento.put(paginaEnvejecimiento, valorActual);
                    
                    // Busca la página con el menor valor de envejecimiento
                    if (valorActual < valorEnvejecimiento) {
                        valorEnvejecimiento = valorActual;
                        victima = paginaEnvejecimiento;
                    }
                }
            }
        }
    }
    
   
    public static void algoritmoEnvejecimiento()
    {
        synchronized(envejecimiento)
        {
            synchronized(paginasEnUso)
            {
                // Iterar sobre el mapa de envejecimiento
                for (Map.Entry<Integer, Integer> paginasEnvejecimiento : envejecimiento.entrySet()) {
                    Integer paginaEnvejecimiento = paginasEnvejecimiento.getKey();
                    Integer bits = paginasEnvejecimiento.getValue();
                    if (!paginasEnUso.contains(paginaEnvejecimiento)) {
                        Integer pagina = paginaEnvejecimiento;
                        // Realizar el corrimiento hacia la izquierda
                        bits <<= 1;
                        // Modificar el primer dígito (poner un 1 en el segundo bit más significativo)
                        bits = (bits & 0x7F) | 0x40;
                        // Actualizar el valor del contador de envejecimiento en el mapa
                        envejecimiento.put(pagina, bits);
                    }
                }
                // Iterar sobre el conjunto de páginas en uso
                for (Integer pagina : paginasEnUso) {
                    Integer bits = envejecimiento.get(pagina);
                    if (bits == null) {
                        // Si la página no está en el mapa de envejecimiento, se agrega con un contador de envejecimiento inicial de 10000000
                        envejecimiento.put(pagina, 10000000);
                    }
                    else {
                        // Si la página ya está en el mapa de envejecimiento, se actualiza su contador de envejecimiento
                        bits = bits / 10;  // Elimina el último dígito
                        bits = 0b10000000 | bits;  // Agrega el 1 al principio
                        envejecimiento.put(pagina, bits);
                    }
                }
            }
        }
        // Se borran las páginas en uso del conjunto de páginas en uso
        paginasEnUso.clear();
    }
    
}