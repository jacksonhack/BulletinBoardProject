package bulletinboard;

public class PrivateBoard implements Board {
    
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
