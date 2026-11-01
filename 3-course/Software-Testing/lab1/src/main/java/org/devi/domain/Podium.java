package org.devi.domain;

/** Помост, на котором может быть закреплён оратор. */
public class Podium extends WorldObject {
    private Person speaker;

    public Podium(String name, Location location) {
        super(name, location);
    }

    /** Возвращаем текущего оратора на помосте. */
    public Person getSpeaker() {
        return speaker;
    }

    /** Назначаем оратора для помоста. */
    public void setSpeaker(Person speaker) {
        this.speaker = speaker;
    }
}
