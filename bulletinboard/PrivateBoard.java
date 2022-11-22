package bulletinboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrivateBoard implements Board {
    List <ClientHandler> connections = new ArrayList<ClientHandler>();
    List <String> users = new ArrayList<String>();
    Map <Integer, Message> messages = new HashMap<Integer, Message>();
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

        // notify all users of new user
        notifyUsers(username + " has joined the board.");
    }

    public void removeUser(String username) {
        users.remove(username);
        System.out.println("User removed. Username: " + username);
        System.out.println("Current users: " + users.size() + "\n\n");

        // notify all users of user leaving
        notifyUsers(username + " has left the board.");
    }

    public void addMessage(Message message) {
        // add message to board, assign it an id in the map, and increment the nextId counter, and notify all users
        messages.put(nextId, message);
        nextId++;

        // notify users that there is a new message with the id of the new message, its sender, post date, and subject
        String notificaiton;

        notificaiton = "New message posted on board " + boardName + " with id " + (nextId - 1) + ". Sender: " + message.sender + ". Date: " + message.date + ". Subject: " + message.subject;

        notifyUsers(notificaiton);
    }

    public Message getMessage(int id) {
        return messages.get(id);
    }

    // get users returns a list of all users on the board
    public List<String> getUsers() {
        return users;
    }

    public String getAllCommands() {
        return """
            Available Commands for private board:\t
            %grouppost -s <subject> -b <body> [posts a messgae with the subject and body to the current board]
            %groupusers [lists all users on the current board ONLY]
            %groupleave OR %leave [leaves the current board]
            %groupmessage <groupName> <messageId> [displays the message with the given id from the given board. NOTE: must be a member of that private board to read a message, unless reading from public board]
            %groups [lists all groups on the server]
            %exit [disconnects from the server]
            %help [lists all available commands and their usages]\t
            """;
    }

    public String getRecentMessages() {
        if(nextId == 0) {
            return "There are no messages on this board yet.";
        } else if (nextId == 1) {
            return "There is only one message on this board. Message id: " + (nextId - 1) + ". Sender: " + messages.get(nextId - 1).sender + ". Date: " + messages.get(nextId - 1).date + ". Subject: " + messages.get(nextId - 1).subject;
        } else {
            return "There are " + nextId + " messages on this board Here are the most recent 2.\t Message id: " + (nextId - 1) + ". Sender: " + messages.get(nextId - 1).sender + ". Date: " + messages.get(nextId - 1).date + ". Subject: " + messages.get(nextId - 1).subject + ".\t Message id: " + (nextId - 2) + ". Sender: " + messages.get(nextId - 2).sender + ". Date: " + messages.get(nextId - 2).date + ". Subject: " + messages.get(nextId - 2).subject;
        }
    }

    // send a string to all users on the board
    private void notifyUsers(String str) {
        for (ClientHandler connection : connections) {
            connection.sendMessage(str);
        }
    }

}
