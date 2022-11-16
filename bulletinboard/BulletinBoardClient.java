/**
 * Programming Assignment 2- Bullietin Board Client
 * Jackson Hacker
 * and
 * Armen Krikorian
 */

package bulletinboard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class BulletinBoardClient {

    public static void main(String[] args) throws Exception {

        // Welcome the user to the bulletin board client
        System.out.println("Welcome to the Bulletin Board Client!");

        while(true) {
            // ask user for %connect command and parse it
            System.out.println("Please enter a %connect command to connect to a server. Format this command as follows: %connect <host> <port>");
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String userCommand = userInput.readLine();

            // check if user entered a %connect command, if not, ask again
            if (!userCommand.startsWith("%connect")) {
                System.out.println("Please use a %connect command first!");
                continue;
            }

            String[] commandTokens = userCommand.split(" ");

            // extract host and port from user command
            String host = commandTokens[1];
            int port = Integer.parseInt(commandTokens[2]);

            awaitServerCommands(host, port);
        }
    }


    private static void awaitServerCommands(String host, int port) throws Exception {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server.");

            // TODO: print list of available commands (usage)

            // TODO: tell user to use %join command to join a board or %groups for list of boards

            // Create input and output streams for the socket.
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create a BufferedReader to read commands from the console.
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Read commands from the console and send them to the server.
            String userInput;
            System.out.print("Enter a command: ");
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Server response: " + in.readLine());
                if (userInput.equals("%exit")) {
                    socket.close();
                    System.out.println("Disconnected from server.");
                    break;
                }
                System.out.println("Enter a command: ");
            }
        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            System.out.println("Please try another %connect command.");
        }
    }
}
