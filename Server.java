import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<Room> rooms = new ArrayList<Room>();
    private static final String MULTICAST = "MULTICAST";
    private static final int PORT = 1234;

    public static void main(String args[]) {
        DatagramSocket aSocket = null;

        try {
            aSocket = new DatagramSocket(PORT);

            System.out.println("Server: listening port UDP/" + PORT);

            byte[] buffer = new byte[1000];

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);

                aSocket.receive(request);

                var command = new String(request.getData(), StandardCharsets.UTF_8);

                var response = handleCommand(command.trim());

                replyClient(aSocket, request, response);

                buffer = new byte[1000];
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }

    private static String handleCommand(String commands) {
        var option = commands.split(" ")[0];

        switch (option) {
            case "list_rooms":
                return listRooms();
            case "create_room":
                return createRoom(commands);
            case "enter_room":
                return enterRoom(commands);
            case "leave_room":
                return leaveRoom(commands);
            case "list_members":
                return listRoomMembers(commands);
            default:
                return invalidCommandReply();
        }
    }

    private static String listRooms() {
        var roomsName = "";

        for (Room r : rooms) {
            roomsName += r.getName() + "\t";
        }

        return "Available rooms: " + roomsName;
    }

    private static String listRoomMembers(String commands) {
        var membersName = "";
        var commandsArray = commands.split(" ");

        if (commandsArray.length < 2) {
            return "Chat name must be sent for this command. enter_room <room_name> <user_name>";
        } else {
            var roomName = commands.split(" ")[1];

            Room room = rooms.stream().filter(r -> roomName.equals(r.getName())).findAny().orElse(null);

            if (room != null) {
                var index = rooms.indexOf(room);

                var members = rooms.get(index).getUsers();

                for (String n : members) {
                    membersName += n + "\t";
                }

                return "Members: " + membersName;

            } else {
                return "Room " + roomName + " not found.";
            }
        }
    }

    private static String createRoom(String commands) {

        var commandsArray = commands.split(" ");

        if (commandsArray.length < 4) {
            return "Three arguments must be sent for this command. create_room <room_name> <multicast_room_ip> <multicast_room_port>";
        } else {
            var roomName = commands.split(" ")[1];
            var roomId = commands.split(" ")[2];
            var roomPort = commands.split(" ")[3];

            var room = new Room(roomName, roomId, roomPort);

            rooms.add(room);

            return "Room " + roomName + " was successfully created";
        }
    }

    private static String enterRoom(String commands) {
        var commandsArray = commands.split(" ");

        if (commandsArray.length < 3) {
            return "Two arguments must be sent for this command. enter_room <room_name> <user_name>";
        } else {
            var roomName = commands.split(" ")[1];
            var userName = commands.split(" ")[2];

            Room room = rooms.stream().filter(r -> roomName.equals(r.getName())).findAny().orElse(null);

            if (room != null) {
                var index = rooms.indexOf(room);

                room.setUser(userName);

                rooms.set(index, room);

                var roomIp = room.getIp();
                var roomPort = room.getPort();

                return String.format("%s %s %s %s %s", MULTICAST, roomName, roomIp, roomPort, userName);

            } else {
                return "Room " + roomName + " not found.";
            }
        }
    }

    private static String leaveRoom(String commands) {
        var commandsArray = commands.split(" ");

        if (commandsArray.length < 2) {
            return "Room name must be sent for this command. leave_room <room_name>";
        } else {
            var roomName = commands.split(" ")[1];

            Room room = rooms.stream().filter(r -> roomName.equals(r.getName())).findAny().orElse(null);

            if (room != null) {
                var index = rooms.indexOf(room);

                rooms.remove(index);

                return "You have successfully left the room.";

            } else {
                return "Room " + roomName + " not found.";
            }
        }
    }

    private static String invalidCommandReply() {
        return "Invalid Command. Please enter the command again.";
    }

    private static void replyClient(DatagramSocket socket, DatagramPacket request, String response) throws IOException {
        DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), request.getAddress(),
                request.getPort());

        socket.send(reply);
    }
}
