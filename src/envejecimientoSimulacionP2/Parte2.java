package envejecimientoSimulacionP2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Parte2 {

    private int MP;
    private List<Integer> paginas;

    public Parte2(Integer MP, List<Integer> paginas) {
        this.MP = MP;
        this.paginas = paginas;
    }

    private static final Object lock = new Object();

    //Los fallos de página serán retornados

    private static Integer fallosPagina = 0;

    //Listas que indican la ubicación de las páginas en memoria real y virtual
    public static List<Integer> paginasMemoriaVirtual = Collections.synchronizedList(new ArrayList<Integer>());
    public static List<Integer> paginasEnUso = Collections.synchronizedList(new ArrayList<Integer>());
    public static List<Integer> paginasMemoriaReal = Collections.synchronizedList(new ArrayList<Integer>());

    //Tabla necesaria para ejecutar el algortimo de envejecimiento
    private static ConcurrentHashMap<Integer, Integer> envejecimiento = new ConcurrentHashMap<Integer, Integer>();

    public Integer ejecutarModo2()
    {
        new AlgoritmoEnvejecimiento().start();
        new Actualizacion().start();
        cargaInicialPaginas();
        Actualizacion.detenerActualizador();
        AlgoritmoEnvejecimiento.detenerEnvejecimiento();
        return fallosPagina;
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