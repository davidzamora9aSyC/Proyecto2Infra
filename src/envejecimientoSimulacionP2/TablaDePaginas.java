package envejecimientoSimulacionP2;

//package envejecimientoSimulacionP2;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TablaDePaginas {
    private final Map<Integer, EntradaTabla> tabla;
    private final Lock lock;

    public TablaDePaginas() {
        this.tabla = new HashMap<>();
        this.lock = new ReentrantLock();
    }
    
    
    public byte obtenerBitsEnvejecimiento(int idPagina) {
        lock.lock();  
        try {
            EntradaTabla entrada = tabla.get(idPagina);
            if (entrada != null) {
                return entrada.bitEnvejecimiento;
            } else {
                throw new IllegalArgumentException("La id de p�gina " + idPagina + " no existe en la tabla");
            }
        } finally {
            lock.unlock();  
        }
    }
    
    private class EntradaTabla {
        int idMarcoPagina;
        byte bitEnvejecimiento;

        EntradaTabla(int idMarcoPagina) {
            this.idMarcoPagina = idMarcoPagina;
            this.bitEnvejecimiento = (byte) 0b00000000;  
        }
    


        void actualizarBitEnvejecimiento(byte valor) {

            this.bitEnvejecimiento = valor;  
        }
	}
    
    public Map<Integer, EntradaTabla> getTabla(){
    	return this.tabla;
    }
    
    public void actualizarBitEnvejecimiento(int idPagina, byte valor) {
        lock.lock();
        try {
            EntradaTabla entrada = tabla.get(idPagina);
            if (entrada != null) {
                entrada.actualizarBitEnvejecimiento(valor);
            }
        } finally {
            lock.unlock();
        }
    }
    
    
    public int encontrarMenosUsado() {
        lock.lock();
        try {
            Integer idPaginaVieja = null;
            byte menorBitEnvejecimiento = Byte.MAX_VALUE;

            for (Entry<Integer, EntradaTabla> entrada : tabla.entrySet()) {
                if (entrada.getValue().bitEnvejecimiento < menorBitEnvejecimiento) {
                    menorBitEnvejecimiento = entrada.getValue().bitEnvejecimiento;
                    idPaginaVieja = entrada.getKey();
                }
            }

            return (idPaginaVieja != null) ? idPaginaVieja : -1;

        } finally {
            lock.unlock();
        }
    }

    
    
    
    
    
    public int getNumeroDeTuplas() {
    	return tabla.size();
    }
    
    
    
    public void agregarEntrada(int idPagina, Integer idMarcoPagina) {
        lock.lock();
        try {
            tabla.put(idPagina, new EntradaTabla(idMarcoPagina));
        } finally {
            lock.unlock();
        }
    }
    
    

    public void eliminarEntradaPorPagina(int idPagina) {
        lock.lock();
        try {
            tabla.remove(idPagina);
        } finally {
            lock.unlock();
        }
    }
    
    

    public Integer obtenerMarcoPagina(int idPagina) {
        lock.lock();
        try {
            EntradaTabla entrada = tabla.get(idPagina);
            if (entrada != null) {
                return entrada.idMarcoPagina;
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

}