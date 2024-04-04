package envejecimientoSimulacionP2;

//AÑADIDOOOO
public class Referencia {
    private int paginaVirtual;
    private int desplazamiento;

    public Referencia(int paginaVirtual, int desplazamiento) {
        this.paginaVirtual = paginaVirtual;
        this.desplazamiento = desplazamiento;
    }

    public int getPaginaVirtual() {
        return paginaVirtual;
    }

    public void setPaginaVirtual(int paginaVirtual) {
        this.paginaVirtual = paginaVirtual;
    }

    public int getDesplazamiento() {
        return desplazamiento;
    }

    public void setDesplazamiento(int desplazamiento) {
        this.desplazamiento = desplazamiento;
    }

    @Override
    public String toString() {
        return "Referencia [paginaVirtual=" + paginaVirtual + ", desplazamiento=" + desplazamiento + "]";
    }
}