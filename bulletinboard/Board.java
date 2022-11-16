package bulletinboard;

import java.util.*;

public interface Board {
    List <ClientHandler> connections = new ArrayList<ClientHandler>();
    List <String> users = new ArrayList<String>();

    public void addConnection(ClientHandler connection);
    public void removeConnection(ClientHandler connection);

    public void addUser(String username);
    public void removeUser(String username);

    // TODO: add method stubs for all functionality, including one for all available commands
}