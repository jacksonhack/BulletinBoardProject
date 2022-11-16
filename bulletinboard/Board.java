package bulletinboard;

import java.util.*;

public interface Board {
    List <ClientHandler> connections = new ArrayList<ClientHandler>();

    public void addConnection(ClientHandler connection);

    public void removeConnection(ClientHandler connection);

    // TODO: add method stubs for all functionality, including one for all available commands
}