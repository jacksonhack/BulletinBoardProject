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
        try (Socket socket = new Socket("localhost", 6789)) {
            System.out.println("Connected to server.");

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
            System.out.println("Error: " + e.getMessage());
        }
    }
}
