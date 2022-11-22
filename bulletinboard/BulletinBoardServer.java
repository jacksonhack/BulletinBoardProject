/**
 * Programming Assignment 2- Bullietin Board Server
 * Jackson Hacker
 * and
 * Armen Krikorian
 */

package bulletinboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Map;

final class ServerConstants {
    public static final int PORT = 6789;
    public static final String PUBLIC_BOARD_NAME = "PublicBoard";
    // hash map of boards and their names
    public static final Map<String, Board> boards = Map.of(
            PUBLIC_BOARD_NAME, new PublicBoard(PUBLIC_BOARD_NAME),
            // four private boards
            "PrivateBoard1", new PrivateBoard("PrivateBoard1"),
            "PrivateBoard2", new PrivateBoard("PrivateBoard2"),
            "PrivateBoard3", new PrivateBoard("PrivateBoard3"),
            "PrivateBoard4", new PrivateBoard("PrivateBoard4")
    );
}

public class BulletinBoardServer {
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = null;
        try {
            // Set the port number.
            int port = ServerConstants.PORT;

            // Establish the listen socket.
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            System.out.println("Starting Bulletin Board server on localhost port " + port + "...");

            // Process requests in an infinite loop, starting a thread for each client connection.
            while(true) {
                // Listen for a TCP connection request.
                Socket client = serverSocket.accept();

                ClientHandler connection = new ClientHandler(client);

                // Create a new thread to process the request.
                Thread thread = new Thread(connection);

                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Server shutting down.");
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }  
    }
}

final class ClientHandler implements Runnable {
    Socket connection;
    Board board;
    String userName;

    PrintWriter out = null;
    BufferedReader in = null;

    // Constructor
    ClientHandler(Socket connection) {
        this.connection = connection;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {

        System.out.println("Connection established.");

        try {
            // Create input and output streams for the socket.
            out = new PrintWriter(connection.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // force user to join a board or disconnect at first
            awaitJoinBoardOrExit(out, in);

            // Read commands from the client and process them.
            awaitBoardCommands(out, in);
        } catch(EarlyDisconnectException e) {
            System.out.println("Client requested disconnect.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    
    private void earlyDisconnect() throws EarlyDisconnectException {
        throw new EarlyDisconnectException("Client disconnected.");
    }

    private void disconnect(){
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
                connection.close();
            }
            if (board != null) {
                board.removeConnection(this);
                board.removeUser(userName);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // await the client joining a board
    private void awaitJoinBoardOrExit(PrintWriter out, BufferedReader in) throws IOException, EarlyDisconnectException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            // ident command will come here, handle it
            if (inputLine.startsWith("%ident")) {
                String[] command = inputLine.split(" ");
                if (command.length != 2) {
                    out.println("Error: invalid command");
                    continue;
                }
                userName = command[1];
                // welcome user to server, send list of available join commands all in one line
                out.println("Welcome to the Bulletin Board Server, " + userName + "!\t" + "Available boards: " + ServerConstants.boards.keySet() + "\tCurrently available commands:\t%join [joins default public board]\t%groupjoin <boardName> [joins specified board]\t%groups [lists available boards]\t%exit [disconnects from server]");
            }
            // await join or groupjoin command to join a board
            else if(inputLine.equals("%join")) {
                // join the public board by default
                String boardName = ServerConstants.PUBLIC_BOARD_NAME;
                board = ServerConstants.boards.get(boardName);
                board.addConnection(this);
                board.addUser(userName);
                out.println("Joined board " + boardName + "\tUsers currently on board: " + board.getUsers()  + "\tMost recent messsages: " + board.getRecentMessages() + "\t\t" + board.getAllCommands());
                return;
            }
            else if(inputLine.startsWith("%groupjoin")) {
                // join board and send welcome message
                // board name follows %groupjoin in inputLine
                String[] command = inputLine.split(" ");
                if (command.length != 2) {
                    out.println("Error: invalid command");
                    continue;
                }
                String boardName = command[1];
                if(ServerConstants.boards.containsKey(boardName)) {
                    board = ServerConstants.boards.get(boardName);
                    board.addConnection(this);
                    board.addUser(userName);
                    out.println("Joined board " + boardName + "\tUsers currently on board: " + board.getUsers() + "\tMost recent messsages: " + board.getRecentMessages() + "\t\t" + board.getAllCommands());
                    return;
                } else {
                    out.println("Board " + boardName + " does not exist.");
                }
            }
            // allow %groups command to list available boards
            else if(inputLine.equals("%groups")) {
                out.println("Available boards: " + ServerConstants.boards.keySet());
            }
            // await exit command to disconnect
            else if(inputLine.startsWith("%exit")) {
                out.println("Disconnecting you from the server...");
                earlyDisconnect();
            }
            else {
                // prompt user to join a board, and list board names
                out.println("Please join a public board using %join or a private board using %groupjoin <boardName>. Available boards:" + ServerConstants.boards.keySet());
            }
        }
    }

    // await the client sending board commands
    private void awaitBoardCommands(PrintWriter out, BufferedReader in) throws IOException, EarlyDisconnectException {
        // Get messages from the client and display them.
        String inputLine;
        while ((inputLine = in.readLine()) != null) {

            // public board only commands
            if(board.getClass() == PublicBoard.class) {
                if (inputLine.startsWith("%post")) {
                    // formatted like %post -s <subject> -b <body>
                    // everything after -s, but before -b is the subject
                    // everything after -b is the body
                    String[] command = inputLine.split(" ");
                    if (command.length < 5) {
                        out.println("Error: invalid command");
                        continue;
                    }
                    // make sure -s is in the right place
                    if(!command[1].equals("-s")) {
                        out.println("Error: invalid command");
                        continue;
                    }
                    // get subject
                    String subject = "";
                    int i = 2;
                    while(!command[i].equals("-b")) {
                        subject += command[i] + " ";
                        i++;
                    }
                    // get body
                    String body = "";
                    i++;
                    while(i < command.length) {
                        body += command[i] + " ";
                        i++;
                    }

                    // build message object
                    Message message = new Message(userName, subject, body);

                    // add message to board
                    board.addMessage(message);

                    // send confirmation to poster
                    out.println("Message posted to board");
                    continue;
                }
                else if (inputLine.startsWith("%users")) {
                    // send a list of all users on the board
                    out.println("Users on current board (" + ServerConstants.PUBLIC_BOARD_NAME + "): " + board.getUsers().toString());
                    continue;
                }
                // formatted like %message <id>
                else if (inputLine.startsWith("%message")) {
                    // send the message with the given id
                    String[] command = inputLine.split(" ");
                    if (command.length != 2) {
                        out.println("Error: invalid command");
                        continue;
                    }
                    int id = Integer.parseInt(command[1]);
                    Message message = board.getMessage(id);
                    if(message == null) {
                        out.println("Error: message with id " + id + " does not exist.");
                    } else {
                        // send the message body as Message <id> from <sender>: <body>
                        out.println("Message " + id + " from " + message.sender + ": " + message.body);
                    }
                    continue;
                }
            }

            // private board only commands
            else if(board.getClass() == PrivateBoard.class) {
                // %grouppost -s <subject> -b <body>
                if (inputLine.startsWith("%grouppost")) {
                    // formatted like %post -s <subject> -b <body>
                    // everything after -s, but before -b is the subject
                    // everything after -b is the body
                    String[] command = inputLine.split(" ");
                    if (command.length < 5) {
                        out.println("Error: invalid command");
                        continue;
                    }
                    // make sure -s is in the right place
                    if(!command[1].equals("-s")) {
                        out.println("Error: invalid command");
                        continue;
                    }
                    // get subject
                    String subject = "";
                    int i = 2;
                    while(!command[i].equals("-b")) {
                        subject += command[i] + " ";
                        i++;
                    }
                    // get body
                    String body = "";
                    i++;
                    while(i < command.length) {
                        body += command[i] + " ";
                        i++;
                    }

                    // build message object
                    Message message = new Message(userName, subject, body);

                    // add message to board
                    board.addMessage(message);

                    // send confirmation to poster
                    out.println("Message posted to board");
                    continue;
                }
                else if (inputLine.startsWith("%groupusers")) {
                    // send a list of all users on the board
                    out.println("Users on current board (" + board.getBoardName() + "): " + board.getUsers().toString());
                    continue;
                }
                // formatted like %groupmessage <groupName> <id>
                // only allow user to view messages from their board or the public board
                else if (inputLine.startsWith("%groupmessage")) {
                    // send the message with the given id
                    String[] command = inputLine.split(" ");
                    if (command.length != 3) {
                        out.println("Error: invalid command");
                        continue;
                    }
                    String groupName = command[1];
                    int id = Integer.parseInt(command[2]);
                    if(groupName.equals(ServerConstants.PUBLIC_BOARD_NAME)) {
                        Message message = ServerConstants.boards.get(ServerConstants.PUBLIC_BOARD_NAME).getMessage(id);
                        if(message == null) {
                            out.println("Error: message with id " + id + " does not exist.");
                        } else {
                            // send the message body as Message <id> from <sender>: <body>
                            out.println("Message " + id + " from " + message.sender + ": " + message.body);
                        }
                    } else if(groupName.equals(board.getBoardName())) {
                        Message message = board.getMessage(id);
                        if(message == null) {
                            out.println("Error: message with id " + id + " does not exist.");
                        } else {
                            // send the message body as Message <id> from <sender>: <body>
                            out.println("Message " + id + " from " + message.sender + ": " + message.body);
                        }
                    } 
                    // if the user is not on the board but the board exists, send an error
                    else if(ServerConstants.boards.containsKey(groupName)) {
                        out.println("Error: you are not a member of board " + groupName + ". Please leave the current board and join the board you wish to view.");
                    }
                    else {
                        // if the board does not exist, send an error
                        out.println("Error: board " + groupName + " does not exist.");
                    }
                    continue;
                }
            }

            // both public and private board commands
            if (inputLine.startsWith("%leave") || inputLine.startsWith("%groupleave")) {
                // remove the user from the board and send a confirmation
                board.removeConnection(this);
                board.removeUser(userName);
                board = null;
                out.println("You have left the board. Use %join or %groupjoin to join another board.");

                // await the user joining another board
                awaitJoinBoardOrExit(out, in);
            }
            // allow %groups command to list available boards
            else if(inputLine.equals("%groups")) {
                out.println("Available boards: " + ServerConstants.boards.keySet());
            }
            else if (inputLine.startsWith("%exit")) {
                // disconnect the client and send a confirmation
                out.println("Disconnecting you from the server...");
                earlyDisconnect();
            }
            else if(inputLine.startsWith("%help")) {
                out.println(board.getAllCommands());
            }
            else {
                out.println("Error: Invalid command.");
            }
        }
    }

    // allow the board to send a message to the client for notificiations
    public void sendMessage(String message) {
        out.println(message);
    }
}

// Early disconnect exception
class EarlyDisconnectException extends Exception {
    public EarlyDisconnectException(String message) {
        super(message);
    }
}