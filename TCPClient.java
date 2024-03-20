import java.io.*;
import java.net.*;
import java.util.Properties;

public class TCPClient {
    private static String SERVER_IP;
    private static int SERVER_PORT;
    private static String LOGIN;
    private static String PASSWORD;

    private String name;

    public static void main(String[] args) {
        getProperties();
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter you name: ");
            String name = consoleReader.readLine();

            // Envia uma mensagem de login;senha;ACK ao servidor
            System.out.print("Enter login: ");
            String login = consoleReader.readLine();

            System.out.print("Enter password: ");
            String password = consoleReader.readLine();

            writer.println(login + ";" + password + ";ACK;" + name);

            // Aguarda a resposta do servidor com o endereço do grupo multicast
            String serverResponse = reader.readLine();
            System.out.println("Server response: " + serverResponse);

            String[] parts = serverResponse.split(":");
            String multicastAddress = parts[0];
            int multicastPort = Integer.parseInt(parts[1]);

            // Inicia um novo cliente para ler as mensagens multicast
            MulticastClient multicastClient = new MulticastClient(multicastAddress, multicastPort);
            multicastClient.start();

            // Thread para enviar mensagens para o servidor
            Thread senderThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = consoleReader.readLine();
                        writer.println(message); // Envia a mensagem para o servidor
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            senderThread.start();

            // Aguarda as threads terminarem
            multicastClient.join();
            senderThread.join();

            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void getProperties() {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream("secure.properties")) {
            properties.load(input);

            // Obtém o valor de uma propriedade específica
            SERVER_IP = properties.getProperty("TCP_IP");
            SERVER_PORT = Integer.parseInt(properties.getProperty("TCP_PORT"));
            LOGIN = properties.getProperty("LOGIN");
            PASSWORD = properties.getProperty("PASSWORD");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MulticastClient extends Thread {
        private String multicastAddress;
        private int multicastPort;

        public MulticastClient(String multicastAddress, int multicastPort) {
            this.multicastAddress = multicastAddress;
            this.multicastPort = multicastPort;
        }

        @Override
        public void run() {
            try {
                MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
                InetAddress group = InetAddress.getByName(multicastAddress);
                multicastSocket.joinGroup(group);

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                System.out.println("Listening for multicast messages...");

                while (true) {
                    multicastSocket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                    System.out.println("Received multicast message: " + received);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
