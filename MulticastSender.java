import java.io.*;
import java.net.*;

public class MulticastSender {
  private static BufferedReader inputLine = null;

  public static void main(String[] args) {
    try {
      inputLine = new BufferedReader(new InputStreamReader(System.in));
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      return;
    }

    receiveServerMessageAndSend();
  }

  public static void receiveServerMessageAndSend(){
    // TCP connection
    Socket clientSocket = null;
    BufferedReader reader = null;

    // Multicast connection
    DatagramSocket multicastSocket = null;
    InetAddress multicastAddress = null;
    int multicastPort = 8888;

    try{
      // Connect to the server
      clientSocket = new Socket("localhost", 2222);
      reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      // Create multicast socket and gets multicast address
      multicastSocket = new DatagramSocket();
      multicastAddress = InetAddress.getByName("224.0.0.2");

      // loop to get and send messages
      String serverMessage;
      while((serverMessage = reader.readLine())!= null){
//        System.out.println("Received: " + serverMessage);
        // Send server message to multicast address and port
        sendMulticastMessage(serverMessage, multicastSocket, multicastAddress, multicastPort);
      }
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    } finally {
      try {
        if (clientSocket!= null) {
          clientSocket.close();
        }
        if (reader!= null) {
          reader.close();
        }
        if (multicastSocket!= null) {
          multicastSocket.close();
        }
      } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
  }

  public static void sendMulticastMessage(String message, DatagramSocket socket, InetAddress address, int port) {
    try {
      byte[] outBuf = message.getBytes();
      DatagramPacket packet = new DatagramPacket(outBuf, outBuf.length, address, port);
      socket.send(packet);
//      System.out.println("Server sends : " + message);
    } catch (IOException e) {
      System.err.println("Error sending multicast message: " + e.getMessage());
    }
  }
}
