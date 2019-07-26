package sample.table.celldescriptions;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public interface ITableColumnCell<S, T> {
    Callback<TableColumn<S, T>, TableCell<S, T>> getCell();
}
