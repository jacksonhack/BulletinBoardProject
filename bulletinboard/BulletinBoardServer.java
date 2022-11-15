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
    // hash map of boards and their names
    public static final Map<String, Board> boards = Map.of(
            "board1", new Board()
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
                else if (inputLine.startsWith("%exit")) {
                    // disconnect the client and send a confirmation
                    out.println("Disconnecting you from the server...");
                    disconnect();
                }
                // if the message is not a command, send back an error message
                else {
                    out.println("Error: Invalid command.");
                }

                // out.println("test");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    public void disconnect() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
                connection.close();
            }
            board.removeConnection(this);
            // interrupt current thread to stop it
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // await the client joining a board
    public void awaitJoinBoardOrExit(PrintWriter out, BufferedReader in) throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            // await join command to join a board
            if(inputLine.startsWith("%join")) {
                // join board and send welcome message
                // board name follows %join in inputLine
                String boardName = inputLine.substring(6);
                if(ServerConstants.boards.containsKey(boardName)) {
                    board = ServerConstants.boards.get(boardName);
                    board.addConnection(this);
                    out.println("Joined board " + boardName);
                    return;
                } else {
                    out.println("Board " + boardName + " does not exist.");
                }
            }
            // await exit command to disconnect
            else if(inputLine.startsWith("%exit")) {
                out.println("Disconnecting you from the server...");
                disconnect();
                return;
            }
            else {
                // prompt user to join a board, and list board names
                out.println("Please join a board. Available boards:" + ServerConstants.boards.keySet());
            }
        }
    }
}