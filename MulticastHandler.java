import java.io.*;
import java.net.*;

public class MulticastHandler implements Runnable {
  private String name;
  private InetAddress MulticastAddress = InetAddress.getByName("224.0.0.2");
  private InetAddress serverAddress;

  private int port = 8888;

  private int login_number = 0;

  private Socket serverSocket = null;

  public MulticastHandler(Socket mastersocket) throws UnknownHostException {
    serverSocket = mastersocket;
    serverAddress = serverSocket.getInetAddress();
  }

    @Override
  public void run() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      //Prepare to join multicast group
      MulticastSocket socket = new MulticastSocket(port);
      socket.joinGroup(MulticastAddress);

      while (true) {
        byte[] inBuf = new byte[256];
        DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
        socket.receive(inPacket);
        String msg[] = new String(inBuf, 0, inPacket.getLength()).split(";");
        System.out.println("<" + msg[0] + ">" + " Msg : " + msg[1]);
      }
    } catch (IOException ioe) {
      System.out.println(ioe);
    }
  }

  public void sendMessage(String name, int login_number, InetAddress server) {
    try {

      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket sendSocket = new DatagramSocket();

      while (true) {
        System.out.print("Digite a mensagem a ser enviada: ");
        String message = reader.readLine();

        if (message.equals("/q")) {
          break;
        }

        message = name + ';' + message + ';' + String.valueOf(login_number);

        byte[] data = message.getBytes();

        DatagramPacket packet = new DatagramPacket(data, data.length, server, 2222);
        sendSocket.send(packet);
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public Socket getSocket() {
      return serverSocket;
  }

  public static void main(String[] args) {
    String answer[] = null;
    InetAddress server_1 = null;

    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Digite o seu nome:");
      String name = reader.readLine();

      // Connecto to server 1
      int port_1 = 2222;
      Socket servertSocket = new Socket("localhost", port_1);

      System.out.print("Digite o login:");
      String user = reader.readLine();
      System.out.print("Digite sua senha:");
      String password = reader.readLine();

      String login = "ACK;" + user + ',' + password;
      try {

        server_1 = servertSocket.getInetAddress();
        byte[] outBuf = login.getBytes();
        DatagramPacket packet = new DatagramPacket(outBuf, outBuf.length, server_1, port_1);
        DatagramSocket serverDatagram = new DatagramSocket();
        serverDatagram.send(packet);

        // Receber a resposta do servidor UDP
        byte[] receiveBuf = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
        serverDatagram.receive(receivePacket);
        answer = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(";");

      } catch (IOException e) {
        System.err.println("Error sending multicast message: " + e.getMessage());
      }

      if (answer != null && answer[0].equals("Login accepted!")) {

        int login_number = Integer.parseInt(answer[1]);

        MulticastHandler handler = new MulticastHandler(servertSocket);

        Thread receiverThread = new Thread(handler);
        receiverThread.start();

        InetAddress finalServer_ = server_1;
        Thread senderThread = new Thread(() -> handler.sendMessage(name, login_number, finalServer_));
        senderThread.start();

      } else {
        if (answer != null)
          System.out.println(answer[0]);
      }
    } catch (IOException e) {
      System.out.println(e);
    }

  }
}
