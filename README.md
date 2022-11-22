# Jackson Hacker and Armen Krikorian
# Networks Project 2: Bulletin Board

## Compiling and Running
Compile and run BulletinBoardServer.java and BulletinBoardClient.java from the parent folder as you would normally compile and run java code (no additional resources required beyond what is in this package), you can use javac, then java `filename` (ex. javac bulletinboard.BulletinBoardServer.java,  then java bulletinboard.BulletinBoardServer), or use the VSCode java coding pack (just press the run button, this is what I recommend). The server must be running before the client can connect. The server will run on port 6789 by default. Follow the instructions printed by the client to connect to the server using the IP address of the server (or just localhost, if running locally, which it probably is) and the port number with the `%connect` command. The client will prompt you for a username as well, and will give you a list of commands to use to join a board. Once you have joined a board, a list of available commands will be printed. You can use the command `%help` at any time to see the list of commands (identical to the commands suggested in the assignment).

## Major Issues
1. Passing multiple lines from server to client
   - Solution: allow the server to pass back tabs whenever new lines are wanted, and have the client sanitize the response, replacing tabs with new lines.
   - Solution: always listen for input from the server using the ready() method on the stream, and read all lines until the stream is no longer ready
2. Having the client always listen for messages from the server while also allowing the user to type commands
    - Solution: in the while loop that listens for user input, check if the server has sent a message using the ready() method on both the stream from the server and the stream from the user. If the server has sent a message, print it out. If the user has sent a message, read it in and send it to the server. If neither, just keep looping. Only prompt the user for more input after the server has sent a message.
    - This unfortunately caused some weirdness with the command line not displaying what the user typed back to them, but the program still works as intended.