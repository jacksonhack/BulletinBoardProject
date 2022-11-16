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
import java.util.HashMap;
import java.util.Map;

final class ServerConstants {
    public static final int PORT = 6789;
    public static final String PUBLIC_BOARD_NAME = "PublicBoard";
    // hash map of boards and their names
    public static final Map<String, Board> boards = Map.of(
            PUBLIC_BOARD_NAME, new PublicBoard()
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
                out.println("Welcome to the Bulletin Board Server, " + userName + "!\t" + "Available boards: " + ServerConstants.boards.keySet() + "\tCurrently available commands:\t%join [joins default public board]\t%groupjoin <board name> [joins specified board]\t%groups [lists available boards]\t%exit [disconnects from server]");
            }
            // await join or groupjoin command to join a board
            else if(inputLine.equals("%join")) {
                // join the public board by default
                String boardName = ServerConstants.PUBLIC_BOARD_NAME;
                board = ServerConstants.boards.get(boardName);
                board.addConnection(this);
                out.println("Joined board " + boardName);
                // TODO: send list of available commands
                return;
            }
            else if(inputLine.startsWith("%groupjoin")) {
                // join board and send welcome message
                // board name follows %join in inputLine
                String boardName = inputLine.substring(6);
                if(ServerConstants.boards.containsKey(boardName)) {
                    board = ServerConstants.boards.get(boardName);
                    board.addConnection(this);
                    out.println("Joined board " + boardName);
                    // TODO: pass back list of available commands
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
        // TODO: split this into sections based on private or public board (or both), add help command
        
        // Get messages from the client and display them.
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            // commands are lead by %, so we check for that and a keyword
            if (inputLine.startsWith("%post")) {
                // read the post subject and body, and add them to the board, and send a confirmation with the message id

            }
            else if (inputLine.startsWith("%users")) {
                // send a list of all users on the board

            }
            else if (inputLine.startsWith("%message")) {
                // read the message id and send the message subject and body
            
            }
            else if (inputLine.startsWith("%leave")) {
                // remove the user from the board and send a confirmation
                board.removeConnection(this);
                board = null;
                out.println("You have left the board. Use %join to join another board.");

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
            // if the message is not a command, send back an error message
            else {
                out.println("Error: Invalid command.");
            }
        }
    }
}

// Early disconnect exception
class EarlyDisconnectException extends Exception {
    public EarlyDisconnectException(String message) {
        super(message);
    }
}