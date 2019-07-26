package sample.table.celldescriptions;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.input.ScrollEvent;

public class CustomScrollEvent {

    public void fireVerticalScroll(int deltaY, Object source, EventTarget target) {

        ScrollEvent newScrollEvent;
        newScrollEvent = new ScrollEvent(source,
                target,
                ScrollEvent.SCROLL,
                0,
                0,
                0,
                0,
                false,
                false,
                false,
                false,
                false,
                false,
                0,
                deltaY,
                0,
                0,
                ScrollEvent.HorizontalTextScrollUnits.CHARACTERS,
                0,
                ScrollEvent.VerticalTextScrollUnits.NONE,
                deltaY,
                0,
                null);

        Event.fireEvent(target, newScrollEvent);
    }
}
