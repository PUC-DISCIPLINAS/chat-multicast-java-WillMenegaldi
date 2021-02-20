import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final String LEAVE_CHAT = "leave_chat";
    private static final String MULTICAST = "MULTICAST";
    private static Scanner read = new Scanner(System.in);
    private static boolean isUserActive = true;
    private static int udpServerPort = 1234;
    private static String userName;

    public static void main(String args[]) {
        DatagramSocket aSocket = null;

        try {
            var serverIp = InetAddress.getLocalHost();
            var buffer = new byte[1000];

            aSocket = new DatagramSocket();

            while (true) {
                System.out.println("\nEnter command: ");
                var input = read.nextLine();

                var request = new DatagramPacket(input.getBytes(), input.length(), serverIp, udpServerPort);
                aSocket.send(request);

                buffer = new byte[1000];

                var reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);

                var response = new String(reply.getData());

                if (response.length() > 0) {
                    if (response.contains(MULTICAST)) {
                        var multicastData = response.split(" ");

                        var multicastRoomName = sanitize(multicastData[1]);
                        var multicastRoomIp = sanitize(multicastData[2]);
                        var multicastRoomPort = sanitize(multicastData[3]);
                        userName = sanitize(multicastData[4]);

                        multicastChat(multicastRoomName, multicastRoomIp, multicastRoomPort, userName);
                    } else {
                        System.out.println(response.trim());
                    }
                }
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    private static String sanitize(String string) {
        return new String(string.trim());
    }

    public static void multicastChat(String roomName, String roomIp, String roomPort, String userName) {
        try {
            var group = InetAddress.getByName(roomIp);
            var port = Integer.parseInt(roomPort);
            var socket = new MulticastSocket(port);

            socket.joinGroup(group);

            var thread = new Thread(new ListenerThread(socket, group, port));

            thread.start();

            System.out.println(
                    "\nWelcome to multicast chat. Room: " + roomName + " Ip: " + roomIp + " Port: " + port + "\n");

            while (true) {
                var message = read.nextLine();

                if (message.equalsIgnoreCase(Client.LEAVE_CHAT)) {
                    isUserActive = false;
                    socket.leaveGroup(group);
                    socket.close();
                    break;
                }

                var messageToSend = userName + ": " + message;

                var buffer = messageToSend.getBytes();

                var datagram = new DatagramPacket(buffer, buffer.length, group, port);

                socket.send(datagram);
            }
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        }
    }

    static class ListenerThread implements Runnable {
        private MulticastSocket socket;
        private InetAddress group;
        private int port;

        ListenerThread(MulticastSocket socket, InetAddress group, int port) {
            this.socket = socket;
            this.group = group;
            this.port = port;
        }

        @Override
        public void run() {
            while (Client.isUserActive) {
                var buffer = new byte[1000];

                var datagram = new DatagramPacket(buffer, buffer.length, group, port);

                try {
                    socket.receive(datagram);

                    var chatMessage = new String(buffer, StandardCharsets.UTF_8);

                    if (!chatMessage.startsWith(userName)) {
                        System.out.println(chatMessage.trim());
                    }
                } catch (IOException e) {
                }
            }
        }
    }
}
