import java.io.*;
import java.net.*;

public class MulticastSender {

  public static void main(String[] args) {
    try {
      // Cria um socket UDP na porta 8888
      DatagramSocket socket = new DatagramSocket(8888);

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
    sendMulticastMessage(receivedMessage, multicastSocket, multicastAddress, multicastPort);
  }

  public static void sendMulticastMessage(String message, MulticastSocket socket, InetAddress address, int port) {
    try {
      byte[] outBuf = message.getBytes();
      DatagramPacket packet = new DatagramPacket(outBuf, outBuf.length, address, port);
      socket.send(packet);
      System.out.println("Mensagem enviada por multicast: " + message);
    } catch (IOException e) {
      System.err.println("Erro ao enviar mensagem multicast: " + e.getMessage());
    }
  }
}
