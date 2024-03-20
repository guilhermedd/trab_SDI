import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
    private static int PORT;
    private static String MULTICASTADDRESS; // Endereço padrão do grupo de multicast
    private static int MULTICASTPORT; // Porta padrão do grupo de multicast

    private static String LOGIN;
    private static String PASSWORD;
    private static MulticastSocket multicastSocket;
    private static InetAddress multicastGroup;

    public static void main(String[] args) {
        getProperties();
        try {
            multicastSocket = new MulticastSocket();
            multicastGroup = InetAddress.getByName(MULTICASTADDRESS);
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

    public static void getProperties() {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream("secure.properties")) {
            properties.load(input);

            // Obtém o valor de uma propriedade específica
            MULTICASTADDRESS = properties.getProperty("MULTICAST_ADDRESS");
            MULTICASTPORT = Integer.parseInt(properties.getProperty("MULTICAST_PORT"));
            PORT = Integer.parseInt(properties.getProperty("TCP_PORT"));
            LOGIN = properties.getProperty("LOGIN");
            PASSWORD = properties.getProperty("PASSWORD");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;
        private boolean firstMessageReceived = false;

        public ClientHandler(Socket socket) {
            try {
                this.clientSocket = socket;
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.writer = new PrintWriter(socket.getOutputStream(), true);
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

                    // Envia o endereço do grupo multicast e a porta para o cliente
                    System.out.println("ACEITO!");
                    writer.println(MULTICASTADDRESS + ":" + MULTICASTPORT);
                    System.out.println("Multicast address and port sent to client: " + MULTICASTADDRESS + ":" + MULTICASTPORT);

                    // Envia mensagem para o grupo de multicast
                    String multicastMessage = message;
                    DatagramPacket packet = new DatagramPacket(multicastMessage.getBytes(), multicastMessage.length(), multicastGroup, MULTICASTPORT);
                    multicastSocket.send(packet);
                    System.out.println("Sent multicast message: " + multicastMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
