package sample.table.celldescriptions;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.input.MouseEvent;

public class MyEventHandler implements EventHandler<MouseEvent> {

    private Double lastYposition;

    public MyEventHandler(Double lastYposition) {

        this.lastYposition = lastYposition;
    }

    @Override
    public void handle(MouseEvent event) {

        double newYposition = event.getSceneY();
        double diff = newYposition - lastYposition;
        lastYposition = newYposition;
        CustomScrollEvent cse = new CustomScrollEvent();
        cse.fireVerticalScroll((int) diff, this, (EventTarget) event.getSource());

    }
}
