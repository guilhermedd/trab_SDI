import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static String multicastAddress = "224.0.0.1"; // Endereço padrão do grupo de multicast
    private static int multicastPort = 12346; // Porta padrão do grupo de multicast

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Criando uma thread para tratar a conexão com o cliente
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            try {
                this.clientSocket = socket;
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.writer = new PrintWriter(socket.getOutputStream(), true);
                // Adiciona o PrintWriter deste cliente ao conjunto de PrintWriter
                clientWriters.add(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message from client: " + message);

                    // Verifica se a mensagem é "admin;admin;ACK"
                    if (message.equals("admin;admin;ACK")) {
                        // Se for, envia o endereço do grupo multicast e a porta
                        writer.println(multicastAddress + ":" + multicastPort);
                        System.out.println("Multicast address and port sent to client: " + multicastAddress + ":" + multicastPort);
                    } else {
                        // Se não for, apenas exibe a mensagem recebida
                        System.out.println("Message from client: " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Remove o PrintWriter deste cliente ao desconectar
                clientWriters.remove(writer);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
