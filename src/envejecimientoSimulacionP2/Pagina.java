package envejecimientoSimulacionP2;

//package envejecimientoSimulacionP2;

public class Pagina {
    private int id;
    private boolean enMemoria;
    private int r;
    private int tp;
    boolean[] contenido;

    public Pagina(int id) {
        this.id = id;
        this.enMemoria = false;
        this.r = 0;
    }


    public Pagina(int id, int tpP) {
        this.id = id;
        this.enMemoria = false;
        this.r = 0;
        this.tp = tpP;
        this.contenido = new boolean[tpP];
        for(int i=0; i<tpP; i++){
            contenido[i] = false;
        }
    }

	public int getId() {
        return id;
    }

    public boolean estaEnMemoria() {
        return enMemoria;
    }

    public void setEnMemoria(boolean enMemoria) {
        this.enMemoria = enMemoria;
    }

    public void setR(int newR) {
        this.r = newR;
    }

    public int getR() {
        return this.r;
    }

    public int getTp() {
        return this.tp;
    }

    public void putRegistro(int desplazamiento) {
        this.contenido[desplazamiento] = true;
    }

    public void quitRegistro(int desplazamiento) {
        this.contenido[desplazamiento] = false;
    }

    public boolean[] getContenido() {
        return this.contenido;
    }

}