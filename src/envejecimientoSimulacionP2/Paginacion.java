package envejecimientoSimulacionP2;

import java.io.BufferedReader;

//package envejecimientoSimulacionP2;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Paginacion {
	
    public static void generarReferencias(int nf, int nc, int tamanoPagina) {
    	
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("referencias.txt"))) {
            writer.write("TP=" + tamanoPagina + "\n");
            writer.write("NF=" + nf + "\n");
            writer.write("NC=" + nc + "\n");
            writer.write("NF_NC_Filtro=" + 3 + "\n");
            int nr = (19*(nc-2)*(nf-2))+2*nc + 2*(nf-2);
            writer.write("NR=" + nr + "\n");
            int np = (int)Math.ceil((double)((2 * (nf * nc) + 9) * 4) / tamanoPagina);
            writer.write("NP=" + np + "\n");

            
            Map<String, Pagina> paginasA = new HashMap<>();
            int contador =2*tamanoPagina;
            Map<Integer, Pagina> paginasID = new HashMap<>();
            for(int i = 0; i < nf; i++) {
                for (int j = 0; j < nc; j++) {
                	
                    int paginaId = contador / tamanoPagina;
                    String clave = i + "-" + j;
                                    
                    if (!paginasID.containsKey(paginaId)) { 
                        Pagina pagina = new Pagina(paginaId);
                        paginasID.put(paginaId, pagina);  
                    }
                    Pagina pagina1 =paginasID.get(paginaId);
                    paginasA.put(clave, pagina1);
                    contador += 4;
                }
            }
            
            Map<String, Pagina> paginasB = new HashMap<>();
            int contador1 = 0;
            for(int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                	
                    int paginaId = contador1 / tamanoPagina;
                    String clave = i + "-" + j;
                                    
                    if (!paginasID.containsKey(paginaId)) { 
                        Pagina pagina = new Pagina(paginaId);
                        paginasID.put(paginaId, pagina);  
                    }
                    Pagina pagina1 =paginasID.get(paginaId);
                    paginasB.put(clave, pagina1);
                    contador1 += 4;
                }
            }
            Map<String, Pagina> paginasC = new HashMap<>();
            for(int i = 0; i < nf; i++) {
                for (int j = 0; j < nc; j++) {
                	
                    int paginaId = contador / tamanoPagina;
                    String clave = i + "-" + j;
                                    
                    if (!paginasID.containsKey(paginaId)) { 
                        Pagina pagina = new Pagina(paginaId);
                        paginasID.put(paginaId, pagina);  
                    }
                    Pagina pagina1 =paginasID.get(paginaId);
                    paginasC.put(clave, pagina1);
                    contador += 4;
                }
            }
            
            
            
            int des;
            
            for (int i = 1; i < nf - 1; i++) {
                for (int j = 1; j < nc - 1; j++) {
                    for (int a = -1; a <= 1; a++) {
                        for (int b = -1; b <= 1; b++) {
                            int i2 = i + a;
                            int j2 = j + b;
                            int a1 = a + 1;
                            int b1 = b + 1;

                            String claveM = i2 + "-" + j2;
                            Pagina paginaM = paginasA.get(claveM);
                            des = (((i2 * nc + j2) * 4) % tamanoPagina)+4;
                            writer.write(String.format("M[%d][%d],%d,%d,R\n", i2, j2, paginaM.getId(), des));

                            String claveF = a1 + "-" + b1;
                            Pagina paginaF = paginasB.get(claveF);
                            des = ((a1 * 3 + b1) * 4)% tamanoPagina ;
                            writer.write(String.format("F[%d][%d],%d,%d,R\n", a1, b1, paginaF.getId(), des));
                        }
                    }

                 
                    String claveR = i + "-" + j;
                    Pagina paginaR = paginasC.get(claveR);
                    des = (((i * nc + j) * 4) % tamanoPagina)*2;
                    writer.write(String.format("R[%d][%d],%d,%d,W\n", i, j, paginaR.getId(), des));
                }
            }

       
            for (int i = 0; i < nc; i++) {
                String claveSuperior = "0-" + i;
                Pagina paginaSuperior = paginasC.get(claveSuperior);
                int desSuperior = (i * 4) % tamanoPagina;
                writer.write(String.format("R[0][%d],%d,%d,W\n", i, paginaSuperior.getId(), desSuperior));

                String claveInferior = (nf - 1) + "-" + i;
                Pagina paginaInferior = paginasC.get(claveInferior);
                int desInferior = (((nf - 1) * nc + i) * 4) % tamanoPagina;
                writer.write(String.format("R[%d][%d],%d,%d,W\n", nf - 1, i, paginaInferior.getId(), desInferior));
            }

           
            for (int i = 1; i < nf - 1; i++) {
                String claveIzquierda = i + "-0";
                Pagina paginaIzquierda = paginasC.get(claveIzquierda);
                int desIzquierda = (i * nc * 4) % tamanoPagina;
                writer.write(String.format("R[%d][0],%d,%d,W\n", i, paginaIzquierda.getId(), desIzquierda));

                String claveDerecha = i + "-" + (nc - 1);
                Pagina paginaDerecha = paginasC.get(claveDerecha);
                int desDerecha = ((i * nc + (nc - 1)) * 4) % tamanoPagina;
                writer.write(String.format("R[%d][%d],%d,%d,W\n", i, nc - 1, paginaDerecha.getId(), desDerecha));
            }


            



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Seleccione una opci n:");
        System.out.println("1. Generaci n de las referencias");
        System.out.println("2. Calcular el n mero de fallas de p gina");
        int opcion = scanner.nextInt();

        if (opcion == 1) {
            System.out.println("Ingrese el tama o de p gina:");
            int tamanoPagina = scanner.nextInt();
            
            System.out.println("Ingrese el n mero de filas de la matriz 1:");
            int nf1 = scanner.nextInt();
            
            System.out.println("Ingrese el n mero de columnas de la matriz 1:");
            int nc1 = scanner.nextInt();
                       

            generarReferencias(nf1, nc1, tamanoPagina);
            
            System.out.println("Se creó un archivo de referencias llamado referencias.txt que se encuentra en la carpeta src > envejecimientoSimulacionP2");
            
        } else if (opcion == 2) {
        	System.out.println("Ingrese el numero de marcos:");
            int numeroDeMarcos = scanner.nextInt();
            scanner.nextLine(); 
      
            System.out.println("Ingrese el nombre del archivo de referencias con el.txt (en nuestro caso es referencias.txt)");
            String nombreArchivo = scanner.nextLine();
           
            Parte2 modo2 = new Parte2(numeroDeMarcos, nombreArchivo);  //"referencias.txt"
            modo2.ejecutarModo2();
            

            scanner.close();
            
        
        }
        else {
            System.out.println("Opci n no v lida");
        }
    }

    public static List<Integer> leerArchivoModo2(String filename) throws IOException {
        List<Integer> paginas = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String linea;
        while ((linea = reader.readLine()) != null) {
            if (!linea.startsWith("TP") && !linea.startsWith("NF") && !linea.startsWith("NC") && !linea.startsWith("NF_NC_Filtro") && !linea.startsWith("NR")&& !linea.startsWith("NP")) {
                String[] partes = linea.split(",");
                int numPagina = Integer.parseInt(partes[1]);
                paginas.add(numPagina);
            }
        }
        reader.close();
        return paginas;
    }

    
}