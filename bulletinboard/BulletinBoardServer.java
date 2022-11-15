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

    // Constructor
    ClientHandler(Socket connection, Board board) {
        this.connection = connection;
        this.board = board;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;

        System.out.println("Connection established.");

        try {
            // Create input and output streams for the socket.
            out = new PrintWriter(connection.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // Get messages from the client and display them.
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // if (inputLine.equals("exit")) {
                //     break;
                // } else if (inputLine.equals("list")) {
                //     out.println(board.list());
                // } else if (inputLine.startsWith("post")) {
                //     board.post(inputLine.substring(5));
                // } else if (inputLine.startsWith("delete")) {
                //     board.delete(Integer.parseInt(inputLine.substring(7)));
                // } else {
                //     out.println("Invalid command.");
                // }

                out.println("test");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
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

}
