package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainServer {
    public static final int PORT = 5000;
    private static ReservationManager manager = new ReservationManager();
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("=== SERVER MANAGEMENT REZERVARI PORUNIT ===");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serverul asculta pe portul: " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Conexiune primita de la IP-ul: " + socket.getRemoteSocketAddress());
                
                ClientHandler handler = new ClientHandler(socket, manager, clients);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Eroare critica la pornirea serverului socket: " + e.getMessage());
        }
    }
}