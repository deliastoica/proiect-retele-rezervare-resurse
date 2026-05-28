package server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReservationManager {
    private List<Resource> resources;

    public ReservationManager() {
        resources = new ArrayList<>();
        resources.add(new Resource("Room1"));
        resources.add(new Resource("Room2"));
        resources.add(new Resource("Room3"));
    }

    public synchronized String listResources() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== STARE ACTUALA RESURSE ===\n");
        for (Resource r : resources) {
            sb.append(r);
        }
        sb.append("=============================\n");
        return sb.toString();
    }

    private boolean overlaps(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }

    public synchronized String lockResource(String resourceName, String username, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            return "ERROR Interval invalid. Data de inceput trebuie sa fie inainte de data de sfarsit.";
        }

        for (Resource resource : resources) {
            if (resource.getName().equalsIgnoreCase(resourceName)) {
                // 1. Verificare suprapunere cu rezervari confirmate
                for (Reservation res : resource.getReservations()) {
                    if (overlaps(start, end, res.getStart(), res.getEnd())) {
                        return "ERROR Suprapunere! Exista deja o rezervare confirmata pe acest interval.";
                    }
                }
                // 2. Verificare suprapunere cu blocari temporare (locks)
                for (Lock lock : resource.getLocks()) {
                    if (overlaps(start, end, lock.getStart(), lock.getEnd())) {
                        return "ERROR Resursa este blocata temporar de altcineva pe acest interval.";
                    }
                }

                resource.getLocks().add(new Lock(username, start, end));
                return "SUCCESS Blocare temporara creata cu succes pentru 5 minute.";
            }
        }
        return "ERROR Resursa specificata nu exista.";
    }

    public synchronized String unlockResource(String resourceName, String username) {
        for (Resource resource : resources) {
            if (resource.getName().equalsIgnoreCase(resourceName)) {
                Iterator<Lock> it = resource.getLocks().iterator();
                while (it.hasNext()) {
                    Lock lock = it.next();
                    if (lock.getUsername().equalsIgnoreCase(username)) {
                        it.remove();
                        return "SUCCESS Blocarea temporara a fost anulata.";
                    }
                }
                return "ERROR Nu ai nicio blocare activa pe aceasta resursa.";
            }
        }
        return "ERROR Resursa specificata nu exista.";
    }

    public synchronized String confirmReservation(String resourceName, String username) {
        for (Resource resource : resources) {
            if (resource.getName().equalsIgnoreCase(resourceName)) {
                Iterator<Lock> it = resource.getLocks().iterator();
                while (it.hasNext()) {
                    Lock lock = it.next();
                    if (lock.getUsername().equalsIgnoreCase(username)) {
                        Reservation res = new Reservation(username, lock.getStart(), lock.getEnd());
                        resource.getReservations().add(res);
                        it.remove(); // Eliminam lock-ul, acum e rezervare oficiala
                        return "SUCCESS Rezervare confirmata definitiv! ID-ul tau este: " + res.getId();
                    }
                }
                return "ERROR Nu ai nicio blocare temporara activa pe aceasta resursa ca sa o poti confirma.";
            }
        }
        return "ERROR Resursa specificata nu exista.";
    }

    public synchronized String deleteReservation(int id, String username) {
        for (Resource resource : resources) {
            Iterator<Reservation> it = resource.getReservations().iterator();
            while (it.hasNext()) {
                Reservation res = it.next();
                if (res.getId() == id) {
                    if (res.getUsername().equalsIgnoreCase(username)) {
                        it.remove();
                        return "SUCCESS Rezervarea ta a fost stearsa.";
                    } else {
                        return "ERROR Nu ai permisiunea sa stergi rezervarea altui utilizator.";
                    }
                }
            }
        }
        return "ERROR Rezervarea cu ID-ul specificat nu a fost gasita.";
    }

    public synchronized String updateReservation(int id, String username, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            return "ERROR Interval invalid.";
        }

        for (Resource resource : resources) {
            for (Reservation res : resource.getReservations()) {
                if (res.getId() == id) {
                    if (!res.getUsername().equalsIgnoreCase(username)) {
                        return "ERROR Nu poti modifica rezervarea altui utilizator.";
                    }

                    // Verificam suprapuneri cu ALTE rezervari active
                    for (Reservation other : resource.getReservations()) {
                        if (other.getId() != id && overlaps(start, end, other.getStart(), other.getEnd())) {
                            return "ERROR Noul interval se suprapune cu o rezervare existenta.";
                        }
                    }

                    // Verificam suprapuneri cu Lock-urile altora
                    for (Lock lock : resource.getLocks()) {
                        if (overlaps(start, end, lock.getStart(), lock.getEnd())) {
                            return "ERROR Noul interval se suprapune cu o blocare temporara a altui utilizator.";
                        }
                    }

                    res.setStart(start);
                    res.setEnd(end);
                    return "SUCCESS Rezervarea a fost modificata cu succes.";
                }
            }
        }
        return "ERROR Rezervarea nu exista.";
    }

    public synchronized void removeUserLocks(String username) {
        if (username == null) return;
        for (Resource resource : resources) {
            resource.getLocks().removeIf(lock -> username.equalsIgnoreCase(lock.getUsername()));
        }
    }
}