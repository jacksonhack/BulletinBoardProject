package bulletinboard;

import java.util.List;

public class PrivateBoard implements Board {
    String boardName;
    Integer nextId;

    public PrivateBoard(String boardName) {
        this.boardName = boardName;
        nextId = 0;
    }

    public String getBoardName() {
        return boardName;
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

    public void addMessage(Message message) {
        // TODO: implement for private boards
    }

    public Message getMessage(int id) {
        // TODO: implement for private boards
        return null;
    }

    // get users returns a list of all users on the board
    public List<String> getUsers() {
        return users;
    }

    public String getAllCommands() {
        // TODO implement for private boards
        return null;
    }

    public String getRecentMessages() {
        // TODO implement for private boards
        return null;
    }

}
