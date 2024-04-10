package envejecimientoSimulacionP2;

import java.util.LinkedList;
import java.util.Random;

public class Apoyo {
    
    private LinkedList<Integer>  guardados;
    public static Pagina[] paginasMemoriaVirtualJ;
    public static boolean[] marcosDePagina;


    public synchronized void actualizacionDeBitR(){

        if (Apoyo.marcosDePagina.length >= 1){
            

            for (int i = 0; i < Apoyo.marcosDePagina.length; i++) {

                if(marcosDePagina[i]){
                    paginasMemoriaVirtualJ[i].setR(0);
                }           
            }
        }
    }

    /**
    public synchronized void pedirActualizar() {
        notify();
    }
    */

    public static synchronized int getIndexNRU(){
        LinkedList<Integer> clase0 = new LinkedList<Integer>();
        LinkedList<Integer> clase1 = new LinkedList<Integer>();
        Random random = new Random();
        int randomIndex;        

        for(Pagina pag: paginasMemoriaVirtualJ){
            if (pag.getR() == 0) {
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

    
    public synchronized void referenciarPagina(int pagina) {

        if (!guardados.contains(pagina))
            guardados.add(pagina);
    }


    public static void setearUnBit(int id, int valor) {

        paginasMemoriaVirtualJ[id].setR(valor);

    }


    public Apoyo(Pagina[] paginasMemoriaVirtualJ, int NP, boolean[] marcosDePagina) {

        Apoyo.paginasMemoriaVirtualJ = paginasMemoriaVirtualJ;
        Apoyo.marcosDePagina = marcosDePagina;
        this.guardados = new LinkedList<Integer>();

        guardados.clear();
    }

}