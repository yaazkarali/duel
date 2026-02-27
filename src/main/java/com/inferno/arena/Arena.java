package com.inferno.arena;

import org.bukkit.Location;

public class Arena {

    private final String id;
    private String name;
    private Location spawn1;
    private Location spawn2;
    private boolean occupied;

    public Arena(String id, String name, Location spawn1, Location spawn2) {
        this.id = id;
        this.name = name;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.occupied = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Location getSpawn1() { return spawn1; }
    public Location getSpawn2() { return spawn2; }
    public boolean isOccupied() { return occupied; }

    public void setName(String name) { this.name = name; }
    public void setSpawn1(Location spawn1) { this.spawn1 = spawn1; }
    public void setSpawn2(Location spawn2) { this.spawn2 = spawn2; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }

    public boolean isReady() {
        return spawn1 != null && spawn2 != null;
    }
}
