package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Lobby {
    private AtomicLong nextId;
    private List<TheaterIdentifier> theaters;

    public Lobby() {
        theaters = new ArrayList<TheaterIdentifier>();
        nextId = new AtomicLong(0);
    }

    public void addTheater(Theater theater) {
        theaters.add(new TheaterIdentifier(String.format("theater%d", nextId.getAndIncrement()), theater));
    }

    public List<TheaterIdentifier> getTheaters() {
        return Collections.unmodifiableList(theaters);
    }
}
