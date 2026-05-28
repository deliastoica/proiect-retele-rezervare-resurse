package client;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerListener extends Thread {
    private BufferedReader in;

    public ServerListener(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("AUTHENTICATED")) {
                    String user = serverMessage.substring(14);
                    System.out.println("\n[OK] Autentificare reusita! Bun venit, " + user + "!");
                    printMenu();
                } else if (serverMessage.startsWith("NOTIFY")) {
                    System.out.println("\n[ANUNT] " + serverMessage.substring(7));
                    System.out.print("Alege optiunea ta > ");
                } else {
                    System.out.println(serverMessage);
                }
            }
        } catch (IOException e) {
            System.out.println("\n[X] Conexiunea cu serverul s-a intrerupt.");
        }
    }

    private void printMenu() {
        System.out.println("\n============= MENIU COMENZI =============");
        System.out.println("1. LIST                            -> Afiseaza starea resurselor");
        System.out.println("2. LOCK [res] [start] [end]        -> Blocheaza (Ex: LOCK Room1 2026-06-01T12:00 2026-06-01T14:00)");
        System.out.println("3. CONFIRM [res]                   -> Confirma blocarea intr-o rezervare permanenta");
        System.out.println("4. UNLOCK [res]                    -> Anuleaza blocarea temporara");
        System.out.println("5. UPDATE [id] [start] [end]       -> Modifica o rezervare existenta detinuta de tine");
        System.out.println("6. DELETE [id]                     -> Sterge o rezervare de-a ta");
        System.out.println("===========================================");
        System.out.print("Alege optiunea ta > ");
    }
}