package org.devi.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Журнал доменных событий в порядке их добавления. */
public class EventLog {
    private final List<String> events = new ArrayList<>();

    /** Добавляет событие. */
    public void add(String event) {
        if (event == null || event.isBlank()) {
            throw new IllegalArgumentException("Событие не должно быть пустым");
        }
        events.add(event);
    }

    /** Возвращает неизменяемый список всех событий. */
    public List<String> events() {
        return Collections.unmodifiableList(events);
    }

    /** Возвращает последнее событие. */
    public String last() {
        if (events.isEmpty()) {
            throw new IllegalStateException("Журнал событий пуст");
        }
        return events.get(events.size() - 1);
    }
}
