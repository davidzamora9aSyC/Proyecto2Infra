package envejecimientoSimulacionP2;

//package envejecimientoSimulacionP2;

public class Pagina {
    private int id;
    private boolean enMemoria;

    public Pagina(int id) {
        this.id = id;
        this.enMemoria = false;
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

}