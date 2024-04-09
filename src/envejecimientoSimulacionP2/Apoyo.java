package envejecimientoSimulacionP2;

import java.util.LinkedList;
import java.util.Random;

public class Apoyo {
    
    private int NP;
    private LinkedList<Integer>  guardados;
    private String estado;
    public static Pagina[] paginasMemoriaVirtualJ;


    public synchronized void actualizacionDeBitR(){

        for (int i = 0; i < this.NP; i++) {

            if(guardados.contains(paginasMemoriaVirtualJ[i].getId())){
                paginasMemoriaVirtualJ[i].setR(1);
            }           
        }

        guardados.clear();

        estado = "Esperando";
        notify();
    }


    
    public synchronized void pedirActualizar() {
        notify();
    }
    

    public static int getIndexNRU(){
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
        if (estado.equals("Actualizando")) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        guardados.add(pagina);
    }


    public Apoyo(Pagina[] paginasMemoriaVirtualJ, int NP) {

        this.NP = NP;
        Apoyo.paginasMemoriaVirtualJ = paginasMemoriaVirtualJ;
        this.guardados = new LinkedList<Integer>();
        this.estado = "Esperando";

        guardados.clear();
    }

}
