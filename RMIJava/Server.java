/*
 * O servidor deve oferecer dois serviços:
 * - Operações com matriz (implementando a interface IMatrix);
 * - Operações com a base de dados (implementando a interface IDatabase)
 */

import java.rmi.RemoteException;

public class Server implements IMatrix, IDatabase {

   public Server() {}


public static void main(String[] args) {    
     try {
        
     } catch (Exception ex) {
        ex.printStackTrace();
     }
   }
   public double[][] sum(double[][] a, double[][] b) throws RemoteException {
      
   }
   public double[][] mult(double[][] a, double[][] b) throws RemoteException {
    return 
   }

   public double[][] randfill(int rows, int cols) throws RemoteException {

   }

}
