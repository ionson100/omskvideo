package sample.table.celldescriptions;


import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableTestName implements ITableColumnCell<Object, String> {
    private double lastYposition = 0d;

    @Override
    public Callback<TableColumn<Object, String>, TableCell<Object, String>> getCell() {
        return param -> new TableCell<Object, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (item != null) {

                    setStyle("-fx-font-style: italic; -fx-font-weight: bold;");
                    setAlignment(Pos.CENTER_LEFT);
                    setText(String.valueOf(item));
                    this.setOnMousePressed(event -> lastYposition = event.getSceneY());

                    this.setOnMouseDragged(new MyEventHandler(lastYposition));
                }
            }
        };
    }
}

