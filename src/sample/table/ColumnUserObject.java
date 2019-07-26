package sample.table;

public class ColumnUserObject {

    public double width;
    public final String columnName;
    public boolean isVisible;

    public ColumnUserObject(double width, String columnName, boolean isVisible) {

        this.width = width;
        this.columnName = columnName;
        this.isVisible = isVisible;
    }
}
