import java.io.*;
import java.net.*;

public class MulticastReceiver {
  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.print("Digite o seu nome:");
    String name = reader.readLine();

    Thread receiverThread = new Thread(() -> {
      MulticastSocket socket = null;
      DatagramPacket inPacket = null;
      byte[] inBuf = new byte[256];

      try {
        //Prepare to join multicast group
        socket = new MulticastSocket(8888);
        //InetAddress address = InetAddress.getByName("224.0.0.1");
        InetAddress address = InetAddress.getByName("224.0.0.2");

        socket.joinGroup(address);

        while (true) {
          inPacket = new DatagramPacket(inBuf, inBuf.length);
          socket.receive(inPacket);
          String msg[] = new String(inBuf, 0, inPacket.getLength()).split((";"));

          System.out.println("<" + msg[0] + ">" + " Msg : " + msg[1]);
        }
      } catch (IOException ioe) {
        System.out.println(ioe);
      }  finally {
        if (socket != null) {
          socket.close();
        }
      }
    });

    Thread sendThread = new Thread(() -> {
      try {
        DatagramSocket sendSocket = new DatagramSocket();

        while (true) {
          System.out.print("Digite a mensagem a ser enviada: ");
          String message = reader.readLine();

          if (message.equals("/q")) {
            break;
          }

          message = name + ';' + message;

          byte[] data = message.getBytes();
          InetAddress group = InetAddress.getByName("224.0.0.2");
          int port = 8888;

          DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
          sendSocket.send(packet);
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    });

    receiverThread.start();
    sendThread.start();

  }
}
