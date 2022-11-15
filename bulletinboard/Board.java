package bulletinboard;

import java.net.*;
import java.util.*;

public class Board {
    List <ClientHandler> connections = new ArrayList<ClientHandler>();
    ServerSocket socket;

    Board(ServerSocket socket) {
        this.socket = socket;
    }

    public void addConnection(ClientHandler connection) {
        connections.add(connection);
        System.out.println("Connection added. Socket: " + connection.connection);
        System.out.println("Current connections: " + connections.size() + "\n\n");
    }

    public void removeConnection(ClientHandler connection) {
        connections.remove(connection);
        System.out.println("Connection removed. Socket: " + connection.connection);
        System.out.println("Current connections: " + connections.size()+ "\n\n");
    }
}