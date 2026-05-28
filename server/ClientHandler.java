package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private ReservationManager manager;
    private List<ClientHandler> clients;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, ReservationManager manager, List<ClientHandler> clients) {
        this.socket = socket;
        this.manager = manager;
        this.clients = clients;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != this && client.username != null) {
                    client.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            out.println("S-A STABILIT CONEXIUNEA. Introdu username-ul tau pentru autentificare:");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Client_" + this.getId();
            }
            username = username.trim();

            out.println("AUTHENTICATED " + username);
            out.println(manager.listResources());
            broadcast("NOTIFY " + username + " s-a conectat la server.");

            String commandLine;
            while ((commandLine = in.readLine()) != null) {
                String[] parts = commandLine.trim().split("\\s+");
                if (parts.length == 0 || parts[0].isEmpty()) continue;

                String action = parts[0].toUpperCase();
                switch (action) {
                    case "LIST":
                        out.println(manager.listResources());
                        break;

                    case "LOCK":
                        if (parts.length != 4) {
                            out.println("ERROR Sintaxa corecta: LOCK [resursa] [data_start] [data_sfarsit]");
                            break;
                        }
                        try {
                            String resName = parts[1];
                            LocalDateTime start = LocalDateTime.parse(parts[2]);
                            LocalDateTime end = LocalDateTime.parse(parts[3]);
                            
                            String response = manager.lockResource(resName, username, start, end);
                            out.println(response);
                            if (response.startsWith("SUCCESS")) {
                                broadcast("NOTIFY [STIRE] " + username + " a blocat temporar resursa " + resName);
                            }
                        } catch (Exception e) {
                            out.println("ERROR Format data invalid ISO_LOCAL_DATE_TIME! Foloseste: YYYY-MM-DDTHH:MM");
                        }
                        break;

                    case "UNLOCK":
                        if (parts.length != 2) {
                            out.println("ERROR Sintaxa corecta: UNLOCK [resursa]");
                            break;
                        }
                        String unlRes = parts[1];
                        String unlResp = manager.unlockResource(unlRes, username);
                        out.println(unlResp);
                        if (unlResp.startsWith("SUCCESS")) {
                            broadcast("NOTIFY [STIRE] " + username + " a anulat blocarea pe resursa " + unlRes);
                        }
                        break;

                    case "CONFIRM":
                        if (parts.length != 2) {
                            out.println("ERROR Sintaxa corecta: CONFIRM [resursa]");
                            break;
                        }
                        String confRes = parts[1];
                        String confResp = manager.confirmReservation(confRes, username);
                        out.println(confResp);
                        if (confResp.startsWith("SUCCESS")) {
                            broadcast("NOTIFY [UPDATE] " + username + " a finalizat cu succes o rezervare pe resursa " + confRes);
                        }
                        break;

                    case "DELETE":
                        if (parts.length != 2) {
                            out.println("ERROR Sintaxa corecta: DELETE [id_rezervare]");
                            break;
                        }
                        try {
                            int id = Integer.parseInt(parts[1]);
                            String delResp = manager.deleteReservation(id, username);
                            out.println(delResp);
                            if (delResp.startsWith("SUCCESS")) {
                                broadcast("NOTIFY [UPDATE] " + username + " a sters rezervarea cu ID-ul " + id);
                            }
                        } catch (NumberFormatException e) {
                            out.println("ERROR ID-ul rezervarii trebuie sa fie un numar intreg.");
                        }
                        break;

                    case "UPDATE":
                        if (parts.length != 4) {
                            out.println("ERROR Sintaxa corecta: UPDATE [id_rezervare] [nou_start] [nou_sfarsit]");
                            break;
                        }
                        try {
                            int id = Integer.parseInt(parts[1]);
                            LocalDateTime nStart = LocalDateTime.parse(parts[2]);
                            LocalDateTime nEnd = LocalDateTime.parse(parts[3]);
                            
                            String updResp = manager.updateReservation(id, username, nStart, nEnd);
                            out.println(updResp);
                            if (updResp.startsWith("SUCCESS")) {
                                broadcast("NOTIFY [UPDATE] " + username + " a modificat intervalul rezervarii cu ID-ul " + id);
                            }
                        } catch (Exception e) {
                            out.println("ERROR Date invalide! Verifica ID-ul sau formatul datei (YYYY-MM-DDTHH:MM).");
                        }
                        break;

                    default:
                        out.println("ERROR Comanda necunoscuta.");
                        break;
                }
            }
        } catch (IOException e) {
            // Prinde deconectarea neasteptata a clientului
        } finally {
            if (username != null) {
                manager.removeUserLocks(username);
                System.out.println("Log: S-a deconectat clientul " + username);
                broadcast("NOTIFY [SISTEM] Utilizatorul '" + username + "' s-a deconectat. Blocajele lui au fost sterse.");
            }
            synchronized (clients) {
                clients.remove(this);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}