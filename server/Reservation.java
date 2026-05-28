package server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {
    private static int counter = 1;

    private int id;
    private String username;
    private LocalDateTime start;
    private LocalDateTime end;

    public Reservation(String username, LocalDateTime start, LocalDateTime end) {
        this.id = counter++;
        this.username = username;
        this.start = start;
        this.end = end;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    public void setStart(LocalDateTime start) { this.start = start; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("[ID: %d | Utilizator: %s | %s -> %s]", id, username, start.format(fmt), end.format(fmt));
    }
}