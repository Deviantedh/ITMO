package org.devi.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainScenarioTest {

    // Проверяет полный сценарий из условия с порядком ключевых событий в журнале.
    @Test
    void shouldReproduceNarrativeScenarioWithOrderedEvents() {
        EventLog eventLog = new EventLog();

        Location building = new Location("Building", null);
        Location secondFloor = new Location("SecondFloor", building);

        Window majesticWindow = new Window("Majestic Window", secondFloor, true, 2);
        Podium podium = new Podium("Podium", secondFloor);
        secondFloor.addObject(majesticWindow);
        secondFloor.addObject(podium);

        Person arthur = new Person("Arthur", building, eventLog);
        Person orator = new Person("Orator", secondFloor, eventLog);

        Crowd crowd = new Crowd("People", secondFloor, eventLog);
        crowd.addMember(new Person("A", secondFloor, eventLog));
        crowd.addMember(new Person("B", secondFloor, eventLog));

        podium.setSpeaker(orator);

        crowd.cheer();
        orator.say("Люди, слушайте меня");
        arthur.slideTo(majesticWindow);

        assertEquals(CrowdState.CHEERING, crowd.getState());
        assertEquals(orator, podium.getSpeaker());
        assertTrue(majesticWindow.isMajestic());
        assertEquals(PersonState.SLIDING, arthur.getState());
        assertEquals(secondFloor, arthur.getLocation());

        List<String> events = eventLog.events();
        assertEquals(3, events.size());
        assertEquals("Толпа разразилась ликующими криками", events.get(0));
        assertEquals("Orator обратился к людям: Люди, слушайте меня", events.get(1));
        assertEquals("Arthur начал скользить к Majestic Window", events.get(2));
    }
}
