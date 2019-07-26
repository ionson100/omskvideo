package sample.workers;

import sample.table.DisplayColumn;
import sample.table.celldescriptions.TableSimpleCell;

public class MError {
    @DisplayColumn(name_column = "данные", width = 2000, ClassTableCell = TableSimpleCell.class)
    public String msg;
}
