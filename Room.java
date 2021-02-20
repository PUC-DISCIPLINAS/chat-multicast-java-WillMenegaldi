import java.util.ArrayList;

public class Room {
    private String name;
    private String ip;
    private String port;
    private ArrayList<String> users;

    public Room(String name, String ip, String port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.users = new ArrayList<String>();
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUser(String user) {
        this.users.add(user);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
