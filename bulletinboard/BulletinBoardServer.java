/**
 * Programming Assignment 2- Bullietin Board Server
 * Jackson Hacker
 * and
 * Armen Krikorian
 */

package bulletinboard;

import java.io.*;
import java.net.*;
import java.util.*;

public class BulletinBoardServer {
    public static void main(String[] args) throws Exception{
        // Set the port number.
        int port = 6789;

        // Establish the listen socket.
        ServerSocket socket = new ServerSocket(port);

        List <Connection> connections = new ArrayList<Connection>();

        Board board = new Board(socket);

        // Process requests in an infinite loop, starting a thread for each client connection.
        while(true) {
            // Listen for a TCP connection request.
            Socket connectionSocket = socket.accept();

            Connection connection = new Connection(connectionSocket, board);

            // Add connection to the list of connections.
            board.addConnection(connection);

            // Create a new thread to process the request.
            Thread thread = new Thread(connection);

            thread.start();
        }
    }
}

final class Connection implements Runnable {
    Socket connection;
    Board board;

    // Constructor
    Connection(Socket connection, Board board) {
        this.connection = connection;
        this.board = board;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        System.out.println("Connection established.");
        // process requests in an infinite loop as they come in
        while(connection.isConnected()) {
            // process commands as they come, break on exit command
            System.out.println("Processing commands...");

            // break after 5 seconds
            try {
                Thread.sleep(5000);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            // close the socket and remove the connection from the list of connections on the board
            System.out.println("Connection closing.");
            connection.close();
            board.removeConnection(this);
        } catch (Exception e) {
            System.out.println("Error closing connection.");
        }
        
    }

}
