/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. When a client leaves the chat room this thread informs also
 * all the clients about that and terminates.
 */
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

class clientThread extends Thread {

  private final Socket serverSocket;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
//  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, Socket serverSocket) {
    this.clientSocket = clientSocket;
    this.serverSocket = serverSocket;
//    this.threads = threads;
//    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
//    clientThread[] threads = this.threads;

    try {
      /*
       * Create input and output streams for this client.
       */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(serverSocket.getOutputStream());

      os.println("Enter your name.");
      String name = is.readLine().trim();
      os.println("Hello " + name
          + " to our chat room.\nTo leave enter /q");

      // avisa todos que o usuário entrou
//      for (int i = 0; i < maxClientsCount; i++) {
//        if (threads[i] != null && threads[i] != this) {
//          threads[i].os.println("*** THe user " + name
//              + " has entered the chat room ***");
//        }
//      }
      os.println("*** THe user " + name
              + " has entered the chat room ***");

      while (true) {
        String line = is.readLine();
        if (line.startsWith("/q")) {
          break;
        }

        // manda a mensagem para todo mundo

        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null) {
            threads[i].os.println("<" + name + ">; " + line);
          }
        }
      }

      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("*** The user " + name
              + " is leaving the chat room !!! ***");
        }
      }
      
      os.println("*** Bye " + name + " ***");

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] == this) {
          threads[i] = null;
        }
      }

      /*
       * Close the output stream, close the input stream, close the socket.
       */
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }

}
