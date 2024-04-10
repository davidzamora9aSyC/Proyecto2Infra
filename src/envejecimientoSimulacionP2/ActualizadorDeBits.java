package envejecimientoSimulacionP2;

public class ActualizadorDeBits extends Thread {
    
    public boolean enPaginacion = true;
    public Apoyo apoyo;

    @Override
    public void run() {

        while(enPaginacion) {
            
            try {
                Thread.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            this.apoyo.actualizacionDeBitR();
        }
    }


    public void detener(){
        this.enPaginacion = false;
    }



    ActualizadorDeBits (Apoyo apoyo){
        this.enPaginacion = true;
        this.apoyo = apoyo;

    }

}
