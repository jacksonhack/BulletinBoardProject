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

public class BulletinBoardServer {
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = null;
        try {
            // Set the port number.
            int port = 6789;

            // Establish the listen socket.
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            Board board = new Board(serverSocket);

            System.out.println("Starting Bulletin Board server on localhost port " + port + "...");

            // Process requests in an infinite loop, starting a thread for each client connection.
            while(true) {
                // Listen for a TCP connection request.
                Socket client = serverSocket.accept();

                ClientHandler connection = new ClientHandler(client, board);

                // Add connection to the list of connections.
                board.addConnection(connection);

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
    ClientHandler(Socket connection, Board board) {
        this.connection = connection;
        this.board = board;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {

        System.out.println("Connection established.");

        try {
            // Create input and output streams for the socket.
            out = new PrintWriter(connection.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

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
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}