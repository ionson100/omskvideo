package sample.table.celldescriptions;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableTestDoubleBlack implements ITableColumnCell<Object, Double> {
    private double lastYposition = 0d;

    @Override
    public Callback<TableColumn<Object, Double>, TableCell<Object, Double>> getCell() {
        return param -> new TableCell<Object, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {


                if (item != null) {
                    getStyleClass().addAll("table-cell");

                    setStyle("-fx-font-size: 30px;-fx-font-weight: bold");

                    setText(String.valueOf(item));
                    setPadding(new Insets(0, 20, 0, 0));
                    setAlignment(Pos.CENTER_RIGHT);
                    this.setOnMousePressed(event -> lastYposition = event.getSceneY());

                    this.setOnMouseDragged(new MyEventHandler(lastYposition));
                }
            }
        };
    }
}
