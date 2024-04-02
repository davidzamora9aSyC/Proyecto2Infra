package envejecimientoSimulacionP2;

//package envejecimientoSimulacionP2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AdminTablaPaginas extends Thread {
    private TablaDePaginas tablaDePaginas;
    private int maxMarcos;
    private String nombreArchivo;
    private int valorActual;
    private boolean modificar;
    
    public boolean isModificar() {
		return modificar;
	}


	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}


	public int getValorActual() {
		return valorActual;
	}


	public void setValorActual(int valor_actual) {
		this.valorActual = valor_actual;
	}


	public AdminTablaPaginas(TablaDePaginas tabla, int maxMarcos, String nombreArchivo) {
        this.tablaDePaginas = tabla;
        this.maxMarcos = maxMarcos;
        this.nombreArchivo = nombreArchivo;
        this.valorActual=-1;
        this.modificar = false;
    }
    
  
	@Override
    public void run() {
        try {
            procesarReferencias();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    		
    
    
	public void procesarReferencias() throws IOException, InterruptedException {
		
	        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
	            
	            int tamanoPaginas = Integer.parseInt(br.readLine().split(":")[1].trim());
	            int numFilas = Integer.parseInt(br.readLine().split(":")[1].trim());
	            int numColsM1 = Integer.parseInt(br.readLine().split(":")[1].trim());
	            int numColsM2 = Integer.parseInt(br.readLine().split(":")[1].trim());
	            int numReferencias = Integer.parseInt(br.readLine().split(":")[1].trim());
	            int numPaginas = Integer.parseInt(br.readLine().split(":")[1].trim());
	
	            String linea;
	            Map<Integer, Boolean> marcos = new HashMap<>();
	            for (int i=0; i<maxMarcos; i++) {
	            	marcos.put(i,false);
	            }
	            
	            int j=0;
	            
	            
	            AlgoritmoDeEnvejecimiento envejecedor = new AlgoritmoDeEnvejecimiento( tablaDePaginas, this);
	            envejecedor.start();
	            while ((linea = br.readLine()) != null) {
	                String[] partes = linea.split(",");
	                String referenciaMatriz = partes[0].trim(); //[A-0-0]
	                int idPagina = Integer.parseInt(partes[1].trim());
	                int posicion = Integer.parseInt(partes[2].trim());
	                valorActual=idPagina;
	                
	                if (tablaDePaginas.obtenerMarcoPagina(idPagina) == null) { //no esta en la tabla
		                modificar = false;
	                	
	                	if (tablaDePaginas.getNumeroDeTuplas()<maxMarcos) {   //hay espacio aun porque quedan marcos libres
	                		Integer marcoLibre = null;   //encuentra el marco lire
	
	                        for (Map.Entry<Integer, Boolean> entrada : marcos.entrySet()) {
	                            if (!entrada.getValue()) {  
	                                marcoLibre = entrada.getKey();
	                                break;
	                            }
	                        }
	                        
	                        tablaDePaginas.agregarEntrada(idPagina, marcoLibre );  //lo agrega
	                        
	                    } else {  //ya se usaron todos los marcos,no esta en la tabla y toca reemplazar
	                    	int aEliminar = tablaDePaginas.encontrarMenosUsado();
	                    	int marcoTemp=tablaDePaginas.obtenerMarcoPagina(aEliminar);
	                    	tablaDePaginas.eliminarEntradaPorPagina(aEliminar);
	                    	tablaDePaginas.agregarEntrada(idPagina, marcoTemp);
	                    }
	                     
	                } else {
		                modificar = true;
	                	valorActual=idPagina;
		                
	                }
	                Thread.sleep(2);
	        	    System.out.println(j);
	        	    j++;
	            }
	        }
	    
	    valorActual=-1;
		
    }

}