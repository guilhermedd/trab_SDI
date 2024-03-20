import java.io.*;
import java.net.*;
public class multicastReceiver {
    public static void main(String[] args) {
        String username = "admin";
        String password = "admin";

        String login = username + "," + password;
        try {
            // Cria um socket UDP na porta 8888
            DatagramSocket socket = new DatagramSocket(2222);

            // Cria um socket de multicast
            MulticastSocket multicastSocket = new MulticastSocket();
            InetAddress multicastAddress = InetAddress.getByName("224.0.0.2");
            int multicastPort = 8000;

            // Loop para receber e retransmitir mensagens
            while (true) {
                receiveAndSend(socket, multicastSocket, multicastAddress, multicastPort);
            }
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    public static void receiveAndSend(DatagramSocket socket, MulticastSocket multicastSocket, InetAddress multicastAddress, int multicastPort) throws IOException {
        byte[] receiveData = new byte[2048];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        // Aguarda a chegada de pacotes
        socket.receive(receivePacket);

        // Extrai os dados recebidos do pacote
        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

        if (receivedMessage.equals("CLOSE")) {
            System.out.println("Servidor fechou a conexão.");
            socket.close();
            return;
        }

        // Envia a mensagem recebida por multicast
//        sendMessage(receivedMessage, multicastSocket, multicastAddress, multicastPort);
    }

    public static String[] sendMessage(String message, DatagramSocket socket, int port, String login) {
        try {
            // Enviar mensagem de confirmação para o servidor TCP
            InetAddress serverAddress = InetAddress.getByName("localhost");
            byte[] sendData = ("ACK;" + login).getBytes();
            DatagramPacket confirmationPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
            socket.send(confirmationPacket);

            // Receber confirmação do servidor
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
            String[] confirmation = receivedMessage.trim().split(";");

            // Verificar se a confirmação é "ok"
            if (confirmation.length > 0 && confirmation[0].equalsIgnoreCase("ok")) {
                // Enviar a segunda mensagem
                byte[] messageData = message.getBytes();
                DatagramPacket messagePacket = new DatagramPacket(messageData, messageData.length, serverAddress, port);
                socket.send(messagePacket);
                System.out.println("Mensagem enviada por multicast: " + message);
                return confirmation;
            } else {
                System.out.println("Erro: Confirmação inválida recebida do servidor.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem multicast: " + e.getMessage());
        }
        return null;
    }

}