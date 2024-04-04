package envejecimientoSimulacionP2;


//AÑADIDOOOO
public class AlgoritmoEnvejecimiento extends Thread {

    private static boolean ejecutarEnvejecimiento;

    public AlgoritmoEnvejecimiento(){
        ejecutarEnvejecimiento = true;
    }

    public static void detenerEnvejecimiento(){
        ejecutarEnvejecimiento = false;
    }
    
    public void run(){

        while (ejecutarEnvejecimiento){

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {e.printStackTrace();}

            Parte2.algoritmoEnvejecimiento();
        }
    }
}
