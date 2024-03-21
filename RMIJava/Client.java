import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/*
 * O cliente deve executar as operações com as matrizes e salvar os dados (recuperar e por fim excluir o arquivo)
 */

public class Client {
    private static double[][] matriz = null;
    private static String host = "localhost";

    // depois dar um jeito de ler a tal matriz 

    public static void main(String[] args) {
        try {
           Registry registry = LocateRegistry.getRegistry(host, 8080);
           
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
