Instructions for compiling and running
--------------------------------------

1. Make sure your current working directory is "Battleships".

2. Compile by running:

        javac @sources.txt

3. Start the server by running:

        java -classpath src server.Server

4. In two or more other terminals, open two or more clients by running:

        java -classpath src view.MatchRoomView


Configuration
-------------

You may change the hostname and port that the server binds to and the client connects to by editing config.properties.
