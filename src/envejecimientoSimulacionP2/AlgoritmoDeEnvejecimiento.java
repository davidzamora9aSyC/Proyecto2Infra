package envejecimientoSimulacionP2;

//package envejecimientoSimulacionP2;

public class AlgoritmoDeEnvejecimiento  extends Thread {
    private TablaDePaginas tablaDePaginas;
    private int numeroDeMarcos;
    private AdminTablaPaginas administrador;

    public AlgoritmoDeEnvejecimiento(TablaDePaginas tablaDePaginas, AdminTablaPaginas administrador) {
        this.tablaDePaginas = tablaDePaginas;
        this.administrador = administrador;
    }
    
    @Override
    public void run(){
    	while(administrador.getValorActual()>=0) {
    		
    	}
    }
	public void ejecutar() {
	    	
	        for (int idPagina : tablaDePaginas.getTabla().keySet()) {
	            byte bitsActuales = tablaDePaginas.obtenerBitsEnvejecimiento(idPagina);
	            bitsActuales >>>= 1;
	
	            
	        }

	    EncontrarElemento(administrador.getValorActual());
	    }
	
	    private void EncontrarElemento(int idPagina) {
	    	byte numeroAcambiar = tablaDePaginas.obtenerBitsEnvejecimiento(idPagina);
	    	 
	    	byte bitConUno =  (byte) ((numeroAcambiar)|0b10000000);
	    	tablaDePaginas.actualizarBitEnvejecimiento(idPagina, bitConUno);
	    }
	}