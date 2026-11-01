package org.devi.domain;

import java.util.UUID;

/** Базовый объект мира с именем и закреплённой локацией. */
public abstract class WorldObject {
    private final UUID id;
    private final String name;
    private final Location location;

    protected WorldObject(String name, Location location) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя объекта не должно быть пустым");
        }
        if (location == null) {
            throw new IllegalArgumentException("Локация объекта не должна быть null");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
