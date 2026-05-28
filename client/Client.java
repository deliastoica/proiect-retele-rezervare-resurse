package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Incearcare de conectare la serverul de rezervari (" + HOST + ":" + PORT + ")...");
        
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            // Pornim thread-ul dedicat exclusiv ascultarii mesajelor venite de la server
            ServerListener listener = new ServerListener(in);
            listener.start();

            // Citim comenzile utilizatorului din consola si le trimitem la server
            while (true) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.trim().equalsIgnoreCase("EXIT")) {
                        System.out.println("Inchidere aplicatie client...");
                        break;
                    }
                    out.println(input);
                }
            }

        } catch (IOException e) {
            System.err.println("Imposibil de conectat la server. Asigura-te ca serverul Docker ruleaza!");
        }
    }
}