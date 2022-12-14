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

    static String screenName;

    public static void main(String[] args) throws Exception {

        // Welcome the user to the bulletin board client
        System.out.println("Welcome to the Bulletin Board Client!");

        // Prompt user for a screen name to be used on bulletin boards
        System.out.println("Please enter a screen name (this will be seen by all other users on a bulletin board): ");

        // Get the screen name from the user
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        screenName = inFromUser.readLine();

        while(true) {
            // ask user for %connect command and parse it
            System.out.println("Please enter a %connect command to connect to a server (will run on localhost port 6789 by default). Format this command as follows: %connect <host> <port>");
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

            // Create input and output streams for the socket.
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create a BufferedReader to read commands from the console.
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // use hidden %ident command to identify user to server
            out.println("%ident " + screenName);

            // Read commands from the console and send them to the server.
            String userInput;
            // while the user has not entered a %exit command, continue to read commands or await a server response
            while (true) {
                // otherwise, if the server has sent a message (notificaiton of messages or response), print it
                if(in.ready()) {
                    while(in.ready()) {
                        System.out.println(replaceTabs(in.readLine()));
                    }
                    // ask user for a command
                    System.out.println("Enter a command and press enter (it may not look like your cursor is moving, but the input is coming through): ");
                }
                // read user input and send to server, if user inputs something
                else if(stdIn.ready()) {
                    userInput = stdIn.readLine();
                    out.println(userInput);
                    if (userInput.equals("%exit")) {
                        socket.close();
                        System.out.println("Disconnected from server.");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            System.out.println("Please try another %connect command.");
        }
    }

    // replace tabs with newlines
    private static String replaceTabs(String s) {
        return s.replace("\t", "\n");
    }
}
