package envejecimientoSimulacionP2;

public class Actualizacion extends Thread {

    private static boolean updateTP;

    public Actualizacion(){
        updateTP = true;
    }

    public static void detenerActualizador(){
        updateTP = false;
    }
    
    public void run(){

        while (updateTP){

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {e.printStackTrace();}

            Parte2.updateTP();

        }
    }
}
