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

    public void addUser(String username) {
        users.add(username);
        System.out.println("User added. Username: " + username);
        System.out.println("Current users: " + users.size() + "\n\n");
    }

    public void removeUser(String username) {
        users.remove(username);
        System.out.println("User removed. Username: " + username);
        System.out.println("Current users: " + users.size() + "\n\n");
    }
}
