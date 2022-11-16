package bulletinboard;

import java.util.*;

public interface Board {
    List <ClientHandler> connections = new ArrayList<ClientHandler>();
    List <String> users = new ArrayList<String>();
    Map <Integer, Message> messages = new HashMap<Integer, Message>();

    public String getBoardName();

    public void addConnection(ClientHandler connection);
    public void removeConnection(ClientHandler connection);

    public void addUser(String username);
    public void removeUser(String username);

    public void addMessage(Message message);
    public Message getMessage(int id);

    // get users returns a list of all users on the board
    public List<String> getUsers();

    public String getAllCommands();

    public String getRecentMessages();
}