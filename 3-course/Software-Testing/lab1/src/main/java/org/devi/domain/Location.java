package org.devi.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Локация в мире: может иметь родительскую локацию и набор объектов. */
public class Location {
    private final UUID id;
    private final String name;
    private final Location parent;
    private final List<WorldObject> objects;

    public Location(String name, Location parent) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название локации не должно быть пустым");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.parent = parent;
        this.objects = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getParent() {
        return parent;
    }

    /** Добавляет объект в локацию, если его локация совпадает с этой локацией. */
    public void addObject(WorldObject object) {
        if (object == null) {
            throw new IllegalArgumentException("Объект не должен быть null");
        }
        if (!Objects.equals(object.getLocation(), this)) {
            throw new IllegalArgumentException("Локация объекта должна совпадать с текущей локацией");
        }
        objects.add(object);
    }

    /** Возвращает неизменяемый список объектов локации. */
    public List<WorldObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /** Ищет объект по имени в пределах текущей локации. */
    public Optional<WorldObject> findObjectByName(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            throw new IllegalArgumentException("Имя объекта не должно быть пустым");
        }
        return objects.stream().filter(o -> o.getName().equals(objectName)).findFirst();
    }
}
