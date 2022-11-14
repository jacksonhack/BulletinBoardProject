package bulletinboard;

import java.net.*;
import java.util.*;

public class Board {
    List <Connection> connections = new ArrayList<Connection>();
    ServerSocket socket;

    Board(ServerSocket socket) {
        this.socket = socket;
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
        System.out.println("Connection added. Socket: " + connection.connection);
        System.out.println("Current connections: " + connections.size() + "\n\n");
    }

    public void removeConnection(Connection connection) {
        connections.remove(connection);
        System.out.println("Connection removed. Socket: " + connection.connection);
        System.out.println("Current connections: " + connections.size()+ "\n\n");
    }
}