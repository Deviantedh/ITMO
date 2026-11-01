package org.devi.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainInvariantsTest {

    // Проверяет валидацию конструктора Person.
    @Test
    void personConstructorValidation() {
        Location square = new Location("Square", null);

        assertThrows(IllegalArgumentException.class, () -> new Person(null, square, null));
        assertThrows(IllegalArgumentException.class, () -> new Person("   ", square, null));
        assertThrows(IllegalArgumentException.class, () -> new Person("Arthur", null, null));
    }

    // Проверяет валидацию аргументов действий Person.
    @Test
    void personActionValidation() {
        EventLog eventLog = new EventLog();
        Location square = new Location("Square", null);
        Person person = new Person("Arthur", square, eventLog);

        assertThrows(IllegalArgumentException.class, () -> person.moveTo(null));
        assertThrows(IllegalArgumentException.class, () -> person.slideTo(null));
        assertThrows(IllegalArgumentException.class, () -> person.say(null));
        assertThrows(IllegalArgumentException.class, () -> person.say("   "));
    }

    // Проверяет позитивный сценарий Person без привязанного EventLog.
    @Test
    void personActionsShouldWorkWithoutEventLog() {
        Location building = new Location("Building", null);
        Location secondFloor = new Location("SecondFloor", building);
        Window window = new Window("Window", secondFloor, true, 2);
        Person person = new Person("Arthur", building, null);

        person.moveTo(secondFloor);
        assertEquals(PersonState.MOVING, person.getState());
        assertEquals(secondFloor, person.getLocation());

        person.say("Фраза");
        assertEquals(PersonState.SPEAKING, person.getState());

        person.slideTo(window);
        assertEquals(PersonState.SLIDING, person.getState());
        assertEquals(secondFloor, person.getLocation());
    }

    // Проверяет валидацию Crowd и работу remove/add с некорректными параметрами.
    @Test
    void crowdValidation() {
        Location square = new Location("Square", null);
        Crowd crowd = new Crowd("People", square, null);

        assertThrows(IllegalArgumentException.class, () -> new Crowd(null, square, null));
        assertThrows(IllegalArgumentException.class, () -> new Crowd("  ", square, null));
        assertThrows(IllegalArgumentException.class, () -> new Crowd("People", null, null));
        assertThrows(IllegalArgumentException.class, () -> crowd.addMember(null));
        assertThrows(IllegalArgumentException.class, () -> crowd.removeMember(null));
    }

    // Проверяет позитивный сценарий Crowd с двумя участниками без EventLog.
    @Test
    void crowdCheerHappyPathWithoutEventLog() {
        Location square = new Location("Square", null);
        Crowd crowd = new Crowd("People", square, null);
        crowd.addMember(new Person("A", square, null));
        crowd.addMember(new Person("B", square, null));

        crowd.cheer();

        assertEquals(2, crowd.size());
        assertEquals(CrowdState.CHEERING, crowd.getState());
        assertEquals(square, crowd.getLocation());
    }

    // Проверяет инвариант: толпа из одного участника не может ликовать.
    @Test
    void crowdWithSingleMemberCannotCheer() {
        EventLog eventLog = new EventLog();
        Location square = new Location("Square", null);
        Crowd crowd = new Crowd("Small Crowd", square, eventLog);
        crowd.addMember(new Person("Only One", square, eventLog));

        assertThrows(IllegalStateException.class, crowd::cheer);
    }

    // Проверяет EventLog: валидация, last() на пустом журнале и неизменяемый список.
    @Test
    void eventLogValidationAndImmutability() {
        EventLog eventLog = new EventLog();

        assertThrows(IllegalArgumentException.class, () -> eventLog.add(null));
        assertThrows(IllegalArgumentException.class, () -> eventLog.add("   "));
        assertThrows(IllegalStateException.class, eventLog::last);

        eventLog.add("первое");
        eventLog.add("второе");

        assertEquals(2, eventLog.events().size());
        assertEquals("второе", eventLog.last());
        assertThrows(UnsupportedOperationException.class, () -> eventLog.events().add("третье"));
    }

    // Проверяет Location: валидацию, поиск отсутствующего объекта и неизменяемость списка объектов.
    @Test
    void locationValidationAndReadOnlyObjects() {
        Location parent = new Location("Building", null);
        Location secondFloor = new Location("SecondFloor", parent);
        Podium podium = new Podium("Podium", secondFloor);

        assertThrows(IllegalArgumentException.class, () -> new Location(null, parent));
        assertThrows(IllegalArgumentException.class, () -> new Location("  ", parent));

        secondFloor.addObject(podium);

        assertEquals(parent, secondFloor.getParent());
        assertFalse(secondFloor.findObjectByName("Unknown").isPresent());
        assertThrows(IllegalArgumentException.class, () -> secondFloor.findObjectByName(null));
        assertThrows(IllegalArgumentException.class, () -> secondFloor.findObjectByName("   "));
        assertThrows(UnsupportedOperationException.class, () -> secondFloor.getObjects().add(podium));
    }

    // Проверяет, что признаки окна читаются корректно.
    @Test
    void windowMajesticFlagShouldBeReadable() {
        Location secondFloor = new Location("SecondFloor", null);
        Window majesticWindow = new Window("Majestic", secondFloor, true, 2);
        Window ordinaryWindow = new Window("Ordinary", secondFloor, false, 2);

        assertTrue(majesticWindow.isMajestic());
        assertFalse(ordinaryWindow.isMajestic());
        assertEquals(2, majesticWindow.getFloorNumber());
    }

    // Проверяет addObject: null и несовпадение локаций объекта.
    @Test
    void locationAddObjectValidation() {
        Location first = new Location("First", null);
        Location second = new Location("Second", null);
        Podium foreignPodium = new Podium("Foreign", second);

        assertThrows(IllegalArgumentException.class, () -> first.addObject(null));
        assertThrows(IllegalArgumentException.class, () -> first.addObject(foreignPodium));
    }

    // Проверяет валидацию конструктора WorldObject через Podium/Window.
    @Test
    void worldObjectValidationViaChildren() {
        Location square = new Location("Square", null);

        assertThrows(IllegalArgumentException.class, () -> new Podium(null, square));
        assertThrows(IllegalArgumentException.class, () -> new Podium(" ", square));
        assertThrows(IllegalArgumentException.class, () -> new Podium("Podium", null));
        assertThrows(IllegalArgumentException.class, () -> new Window("Window", square, true, 0));
    }

    // Проверяет позитивный сценарий Podium: назначение и снятие оратора.
    @Test
    void podiumSpeakerSetAndClear() {
        Location square = new Location("Square", null);
        Podium podium = new Podium("Podium", square);
        Person orator = new Person("Orator", square, null);

        podium.setSpeaker(orator);
        assertEquals(orator, podium.getSpeaker());

        podium.setSpeaker(null);
        assertNull(podium.getSpeaker());
    }

    // Проверяет базовые геттеры id/name/location у объектов мира и локации.
    @Test
    void worldObjectAndLocationGettersShouldReturnValues() {
        Location square = new Location("Square", null);
        Window window = new Window("Window", square, true, 2);

        assertNotNull(square.getId());
        assertEquals("Square", square.getName());
        assertNull(square.getParent());

        assertNotNull(window.getId());
        assertEquals("Window", window.getName());
        assertEquals(square, window.getLocation());
    }

    // Проверяет, что при наличии журнала позитивный сценарий пишет события в правильном порядке.
    @Test
    void personAndCrowdShouldLogSuccessfulActions() {
        EventLog eventLog = new EventLog();
        Location square = new Location("Square", null);
        Window window = new Window("Window", square, true, 2);
        Person arthur = new Person("Arthur", square, eventLog);
        Person orator = new Person("Orator", square, eventLog);
        Crowd crowd = new Crowd("People", square, eventLog);
        crowd.addMember(new Person("A", square, null));
        crowd.addMember(new Person("B", square, null));

        crowd.cheer();
        orator.say("Речь");
        arthur.slideTo(window);

        assertEquals("Толпа разразилась ликующими криками", eventLog.events().get(0));
        assertEquals("Orator обратился к людям: Речь", eventLog.events().get(1));
        assertEquals("Arthur начал скользить к Window", eventLog.events().get(2));
    }

    // Проверяет, что moveTo меняет локацию/состояние и пишет событие в журнал.
    @Test
    void moveToShouldChangeLocationAndStateAndLogEvent() {
        EventLog eventLog = new EventLog();
        Location building = new Location("Building", null);
        Location secondFloor = new Location("SecondFloor", building);
        Person arthur = new Person("Arthur", building, eventLog);

        arthur.moveTo(secondFloor);

        assertEquals(secondFloor, arthur.getLocation());
        assertEquals(PersonState.MOVING, arthur.getState());
        assertEquals("Arthur переместился в SecondFloor", eventLog.last());
    }

    // Проверяет, что объект добавляется в локацию и доступен через поиск по имени.
    @Test
    void addObjectShouldPutObjectIntoLocation() {
        Location secondFloor = new Location("SecondFloor", null);
        Podium podium = new Podium("Podium", secondFloor);

        secondFloor.addObject(podium);

        assertEquals(1, secondFloor.getObjects().size());
        assertEquals(podium, secondFloor.getObjects().get(0));
        assertTrue(secondFloor.findObjectByName("Podium").isPresent());
    }

    // Проверяет удаление участника и неизменяемость множества, возвращаемого getMembers.
    @Test
    void removeMemberShouldUpdateMembersAndGetMembersShouldBeUnmodifiable() {
        EventLog eventLog = new EventLog();
        Location square = new Location("Square", null);
        Crowd crowd = new Crowd("People", square, eventLog);
        Person first = new Person("A", square, eventLog);
        Person second = new Person("B", square, eventLog);

        crowd.addMember(first);
        crowd.addMember(second);
        crowd.removeMember(first);

        assertEquals(1, crowd.size());
        assertTrue(crowd.getMembers().contains(second));
        assertFalse(crowd.getMembers().contains(first));
        assertThrows(UnsupportedOperationException.class,
                () -> crowd.getMembers().add(new Person("C", square, eventLog)));
    }

    // Проверяет, что last() возвращает последнее добавленное событие.
    @Test
    void eventLogLastShouldReturnMostRecentEvent() {
        EventLog eventLog = new EventLog();
        eventLog.add("событие 1");
        eventLog.add("событие 2");

        assertEquals("событие 2", eventLog.last());
    }

    // Проверяет базовые геттеры идентификатора и имени у Person и Crowd.
    @Test
    void personAndCrowdGettersShouldReturnIdAndName() {
        EventLog eventLog = new EventLog();
        Location square = new Location("Square", null);
        Person person = new Person("Arthur", square, eventLog);
        Crowd crowd = new Crowd("People", square, eventLog);

        assertNotNull(person.getId());
        assertEquals("Arthur", person.getName());
        assertNotNull(crowd.getId());
        assertEquals("People", crowd.getName());
    }
}
