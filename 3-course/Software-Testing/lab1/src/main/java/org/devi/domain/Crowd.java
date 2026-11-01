package org.devi.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/** Группа людей в общей локации. */
public class Crowd {
    private final UUID id;
    private final String name;
    private final Set<Person> members;
    private final Location location;
    private CrowdState state;
    private final EventLog eventLog;

    public Crowd(String name, Location location, EventLog eventLog) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название толпы не должно быть пустым");
        }
        if (location == null) {
            throw new IllegalArgumentException("Локация толпы не должна быть null");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.location = location;
        this.members = new LinkedHashSet<>();
        this.state = CrowdState.CALM;
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

    public CrowdState getState() {
        return state;
    }

    /** Добавляет участника в толпу. */
    public void addMember(Person member) {
        if (member == null) {
            throw new IllegalArgumentException("Участник толпы не должен быть null");
        }
        members.add(member);
    }

    /** Удаляет участника из толпы. */
    public void removeMember(Person member) {
        if (member == null) {
            throw new IllegalArgumentException("Участник толпы не должен быть null");
        }
        members.remove(member);
    }

    /** Возвращает текущее количество участников. */
    public int size() {
        return members.size();
    }

    /** Возвращает неизменяемый набор участников. */
    public Set<Person> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    /** Переводит толпу в состояние ликования. */
    public void cheer() {
        if (members.size() < 2) {
            throw new IllegalStateException("Для ликования в толпе должно быть минимум 2 участника");
        }
        this.state = CrowdState.CHEERING;
        if (eventLog != null) {
            eventLog.add("Толпа разразилась ликующими криками");
        }
    }
}
