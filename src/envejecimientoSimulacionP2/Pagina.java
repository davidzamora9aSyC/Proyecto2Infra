package envejecimientoSimulacionP2;

//package envejecimientoSimulacionP2;

public class Pagina {
    private int id;
    private boolean enMemoria;
    private int r;

    public Pagina(int id) {
        this.id = id;
        this.enMemoria = false;
        this.r = 0;
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

}