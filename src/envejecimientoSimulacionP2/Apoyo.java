package envejecimientoSimulacionP2;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class Apoyo {
    
    private int NP;
    private int[] rBits;
    private LinkedList<Integer>  guardados;
    private String estado;
    public static Pagina[] paginasMemoriaVirtualJ;

    /**
     * Este método se encarga de actualizar los bits R de las páginas.
    
    public synchronized void actualizarRBits() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        estado = "Actualizando";
        for (int i = 0; i < rBits.length; i++) {
            // Desplazar bits a la derecha.
            rBits[i] = rBits[i]>>1;
            if (guardados.contains(i)) {
                // Encender el octavo bit.
                rBits[i] += 1<<7;
            }
        }
        // Limpiar lista de páginas referenciadas.
        guardados.clear();

        estado = "Esperando";
        notify();
    }
    */

    public synchronized void actualizacionDeBitR(){

        for (int i = 0; i < this.NP; i++) {

            if(guardados.contains(paginasMemoriaVirtualJ[i].getR())){
                paginasMemoriaVirtualJ[i].setR(1);
            }           
        }

        guardados.clear();

        estado = "Esperando";
        notify();
    }

    /*
     * Este método se encarga de pedir la actualización de los bits R.
    */
    public synchronized void pedirActualizar() {
        notify();
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

    
    /**
     * Este método se encarga de obtener el indice de la página a eliminar.
     * @param tablaPaginas: tabla de páginas.
     * @return índice de la página a eliminar.
   
    public synchronized int obtenerPaginaAEliminar(int[] tablaPaginas) {
        if (estado.equals("Actualizando")) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Encontrar la página con el conteo mas bajo, en caso de empate, se elige la primera.
        int idx = 0, menor = 256;
        for (int j = 0; j < tablaPaginas.length; j++) {
            if (tablaPaginas[j] != -1 && this.rBits[j] < menor) {
                menor = this.rBits[j];
                idx = j;
            }
        }
        return idx;
    }
    */

    /*
     * Este método se encarga de referenciar una página, añadiendo su número a la lista de páginas referenciadas.
     * @param pagina: índice de la página a referenciar.
    */
    public synchronized void referenciarPagina(int pagina) {
        if (estado.equals("Actualizando")) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        guardados.add(pagina);
    }

    /*
     * Constructor de la clase.
     * @param np: número de páginas.
    */
    public Apoyo(Pagina[] paginasMemoriaVirtualJ, int NP) {

        this.NP = NP;
        this.rBits = new int[NP];
        Apoyo.paginasMemoriaVirtualJ = paginasMemoriaVirtualJ;
        this.guardados = new LinkedList<Integer>();
        this.estado = "Esperando";

        for (int i = 0; i < rBits.length; i++) {
            rBits[i] = 0;
        }
        guardados.clear();
    }

}
