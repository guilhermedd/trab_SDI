import java.io.*;
import java.net.*;
import java.util.Properties;

import static java.lang.Thread.sleep;

public class MultiThreadChatServer {

  private static final int maxClientsCount = 20;

  public static void main(String args[]) {
    int portNumber = 2222;
    int multicastPort = 8889;

    try {
      Properties properties = new Properties();
      FileInputStream fileInputStream = new FileInputStream("login.properties");
      properties.load(fileInputStream);
      fileInputStream.close();

      String user = properties.getProperty("user");
      String password = properties.getProperty("password");
      String master_user = properties.getProperty("master_user");
      String master_password = properties.getProperty("master_password");

      DatagramSocket socket = new DatagramSocket(portNumber);

      byte[] buffer = new byte[2048];

      while (true) {
        // Cria um pacote para receber os dados
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

        // Aguarda a chegada de pacotes
        socket.receive(receivePacket);

        String login = new String(receivePacket.getData(), 0, receivePacket.getLength());

        System.out.println(login);

        byte[] sendData;

        // Message type: name;user,password
        if (!login.split(";")[1].equals(user + ',' + password) && !login.split(";")[1].equals(master_user + ',' + master_password)) {
          String rejection = "The login is incorrect!;";
          sendData = rejection.getBytes();
          InetAddress clientAddress = receivePacket.getAddress();
          int clientPort = receivePacket.getPort();
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
          socket.send(sendPacket);
          sleep(500);
        } else {
          // Send OK
          String accepted = "OK;224.0.0.2;8000"; // accepted; multicast address; port
          sendData = accepted.getBytes();
          InetAddress clientAddress = receivePacket.getAddress();
          int clientPort = receivePacket.getPort();
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
          socket.send(sendPacket);
          sleep(500);

          // Get the message
          socket.receive(receivePacket);
          String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

          // Send the message to Multicast Server
          InetAddress multicastAddress = InetAddress.getByName("localhost");
          sendData = receivedMessage.getBytes();
          DatagramPacket sendToMulticast = new DatagramPacket(sendData, sendData.length, multicastAddress, multicastPort);
          socket.send(sendToMulticast);
          sleep(500);
        }
      }
    } catch (IOException e) {
      System.out.println("Erro: " + e.getMessage());
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
  }
}
