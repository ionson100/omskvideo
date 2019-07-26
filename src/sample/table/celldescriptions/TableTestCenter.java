package sample.table.celldescriptions;


import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableTestCenter implements ITableColumnCell<Object, Object> {
    private double lastYposition = 0d;

    @Override
    public Callback<TableColumn<Object, Object>, TableCell<Object, Object>> getCell() {
        return param -> new TableCell<Object, Object>() {

            @Override
            protected void updateItem(Object item, boolean empty) {
                if (item != null) {

                    setAlignment(Pos.CENTER);
                    setText(String.valueOf(item));
                    this.setOnMousePressed(event -> lastYposition = event.getSceneY());

                    this.setOnMouseDragged(new MyEventHandler(lastYposition));
                }
            }
        };
    }
}

