package org.devi.domain;

import java.util.UUID;

public class Person {
    private final UUID id;
    private final String name;
    private Location location;
    private PersonState state;
    private final EventLog eventLog;

    public Person(String name, Location location, EventLog eventLog) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя персонажа не должно быть пустым");
        }
        if (location == null) {
            throw new IllegalArgumentException("Локация персонажа не должна быть null");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.location = location;
        this.state = PersonState.IDLE;
        this.eventLog = eventLog;
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

    public PersonState getState() {
        return state;
    }

    /** Перемещает персону в новую локацию и фиксирует событие. */
    public void moveTo(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Локация назначения не должна быть null");
        }
        this.location = location;
        this.state = PersonState.MOVING;
        if (eventLog != null) {
            eventLog.add(name + " переместился в " + location.getName());
        }
    }

    /** Переводит персону в состояние скольжения к целевому объекту. */
    public void slideTo(WorldObject target) {
        if (target == null) {
            throw new IllegalArgumentException("Целевой объект не должен быть null");
        }
        this.state = PersonState.SLIDING;
        this.location = target.getLocation();
        if (eventLog != null) {
            eventLog.add(name + " начал скользить к " + target.getName());
        }
    }

    /** Переводит персону в состояние речи и фиксирует произнесённую фразу. */
    public void say(String phrase) {
        if (phrase == null || phrase.isBlank()) {
            throw new IllegalArgumentException("Фраза не должна быть пустой");
        }
        this.state = PersonState.SPEAKING;
        if (eventLog != null) {
            eventLog.add(name + " обратился к людям: " + phrase);
        }
    }
}
