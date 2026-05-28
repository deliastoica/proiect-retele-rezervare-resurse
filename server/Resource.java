package server;

import java.util.ArrayList;
import java.util.List;

public class Resource {
    private String name;
    private List<Reservation> reservations;
    private List<Lock> locks;

    public Resource(String name) {
        this.name = name;
        this.reservations = new ArrayList<>();
        this.locks = new ArrayList<>();
    }

    public String getName() { return name; }
    public List<Reservation> getReservations() { return reservations; }
    public List<Lock> getLocks() { return locks; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(">>> RESURSA: ").append(name.toUpperCase()).append("\n");
        
        sb.append("  Rezervari active:\n");
        if (reservations.isEmpty()) {
            sb.append("    (fara rezervari)\n");
        } else {
            for (Reservation r : reservations) {
                sb.append("    ").append(r).append("\n");
            }
        }

        sb.append("  Blocari temporare (Locks):\n");
        if (locks.isEmpty()) {
            sb.append("    (fara blocari active)\n");
        } else {
            for (Lock l : locks) {
                sb.append("    ").append(l).append("\n");
            }
        }
        return sb.toString();
    }
}