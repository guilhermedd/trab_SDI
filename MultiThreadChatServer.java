import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Properties;

/*
 * A chat server that delivers public and private messages.
 */
public class MultiThreadChatServer {

  // The server socket.
  private static ServerSocket serverSocket = null;
  // The client socket.
  private static Socket clientSocket = null;

  private static Socket masterSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections.
  private static final int maxClientsCount = 10;
  private static final clientThread[] threads = new clientThread[maxClientsCount + 1];

  public static void main(String args[]) throws IOException {

    // The default port number.
    int portNumber = 2222;

    if (args.length < 1) {
      System.out.println("Usage: java MultiThreadChatServer <portNumber>\n" + "Now using port number=" + portNumber);
    } else {
      portNumber = Integer.parseInt(args[0]);
    }

    Properties properties = new Properties();

    // Carregar o arquivo de propriedades
    FileInputStream fileInputStream = new FileInputStream("login.properties");
    properties.load(fileInputStream);
    fileInputStream.close();

    // Obter os valores das propriedades "user" e "password"
    String user = properties.getProperty("user");
    String password = properties.getProperty("password");
    String master_user = properties.getProperty("master_user");
    String master_password = properties.getProperty("master_password");

    String get_login = "SEND_LOGIN";
    /*
     * Open a server socket on the portNumber (default 2222). Note that we can not
     * choose a port less than 1023 if we are not privileged users (root).
     */
    try {
      serverSocket = new ServerSocket(portNumber);
      System.out.println("Servidor TCP iniciado na porta " + portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    /*
     * Create a client socket for each connection and pass it to a new client thread.
     */
    while (true) {
      try {
        clientSocket = serverSocket.accept();
        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write(get_login.getBytes());
        boolean accepted = false;

        while (true) {
          // Cria um BufferedReader para ler mensagens enviadas pelo cliente
          BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

          // Lê a mensagem enviada pelo cliente
          String login = reader.readLine();

          if (login != null && (!login.equals(user + password) || !login.equals(master_user + master_password))) {
            String rejection = "The login is incorrect!";
            outputStream.write(rejection.getBytes());
            clientSocket.close();
            break;
          }

          if (login != null && login.equals(user + password)) {
            accepted = true;
            break;
          }

          if (login != null && login.equals(master_user + master_password)) {
            accepted = true;
            masterSocket = clientSocket;
            break;
          }
        }

        if (accepted) {
          Thread clientThread = new Thread(new clientThread(clientSocket, masterSocket));
          clientThread.start();

          //          int i = 0;
//
//          for (i = 1; i < maxClientsCount + 1; i++) { // First client is MulticastSender
//            if (threads[i] == null) {
//              System.out.println("Novo cliente conectado: " + clientSocket);
//              (threads[i] = new clientThread(clientSocket, threads)).start();
//              break;
//            }
//          }
//
//          if (i == maxClientsCount) {
//            PrintStream os = new PrintStream(clientSocket.getOutputStream());
//            os.println("Server too busy. Try later.");
//            os.close();
//            clientSocket.close();
//          }

        }

      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}
