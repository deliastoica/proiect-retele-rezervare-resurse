package server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Lock {
    private String username;
    private LocalDateTime start;
    private LocalDateTime end;

    public Lock(String username, LocalDateTime start, LocalDateTime end) {
        this.username = username;
        this.start = start;
        this.end = end;
    }

    public String getUsername() { return username; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("[Utilizator: %s | %s -> %s]", username, start.format(fmt), end.format(fmt));
    }
}