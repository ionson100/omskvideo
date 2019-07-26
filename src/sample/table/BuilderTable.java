package sample.table;



import com.sun.scenario.Settings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.log4j.Logger;
import sample.table.celldescriptions.ITableColumnCell;
import utils.SettingsApp;
import utils.UtilsOmsk;

import java.lang.reflect.Field;
import java.util.*;

public class BuilderTable {

    private static final Map<Class, List<InnerFieldData>> map = new HashMap<>();
    private static final Logger log = Logger.getLogger(BuilderTable.class);

    public ListChangeListener listener;
    private TableView table;

    private List<InnerFieldData> getFields(Class aClass) {
        if (map.containsKey(aClass) == false) {
            List<InnerFieldData> fieldDatas = new ArrayList<>();
//            UtilsOmsk.getAllFields(aClass);
//            Field[] allFields = aClass.getDeclaredFields();

            for (Field field : UtilsOmsk.getAllFields(aClass)) {

                DisplayColumn displayName = field.getAnnotation(DisplayColumn.class);
                if (displayName == null) continue;

                InnerFieldData d = new InnerFieldData();


                if (displayName.ClassTableCell().isInterface()) {

                } else {

                    try {
                        Class c = Class.forName(displayName.ClassTableCell().getName());
                        d.columnCell = (ITableColumnCell) c.newInstance();
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
                d.field = field;
                d.index = displayName.index();
                d.name = displayName.name_column();
                d.width = displayName.width();
                d.dateFormat = displayName.date_format();
                fieldDatas.add(d);
            }
            fieldDatas.sort(Comparator.comparingInt(o -> o.index));
            map.put(aClass, fieldDatas);
        }
        return map.get(aClass);
    }

    public <T> void build(List<T> list, TableView tableView) {
        table = tableView;
        table.getItems().clear();
        tableView.getColumns().clear();
        tableView.refresh();
        if (listener != null) {
            tableView.getColumns().removeListener(listener);
        }

        if (list == null || list.size() == 0) return;
        Object o = list.get(0);
        List<ColumnUserObject> objectList = SettingsApp.getInstance().getColumnUserObjects(o.getClass().getName());


        List<InnerFieldData> fieldDatas = getFields(o.getClass());


        Class proxyClass = null;
        try {
            proxyClass = Creator.LoadClass(o.getClass().getName() + "_assa2", "assa2", o.getClass().getClassLoader());

        } catch (Exception e) {
            //CreateClass(Class<?> c, String name, List<String> methods, List<String> interfaces, List<String> fields, String directory)
            if (e.getClass() == ClassNotFoundException.class) {
                Creator.CreateClass(o.getClass(), o.getClass().getName() + "_assa2", Creator.GetMethodList(fieldDatas),
                        null, null, "assa2");
            }

        }

        try {
            proxyClass = Creator.LoadClass(o.getClass().getName() + "_assa2", "assa2", o.getClass().getClassLoader());
        } catch (Exception e) {
            log.error(e);
        }
        tableView.getColumns().clear();


        CreateColumns(tableView, fieldDatas, objectList);


        listener = c -> refreshColumnList(table, objectList);
        tableView.getColumns().addListener(listener);


        for (Object o1 : tableView.getColumns()) {

            TableColumn col = (TableColumn) o1;
            col.visibleProperty().addListener((observable, oldValue, newValue) -> {
                refreshColumnList(table, objectList);


            });
            col.widthProperty().addListener((ov, t, t1) -> {
                refreshColumnList(table, objectList);


            });
        }

        ObservableList listRes = null;
        try {
            listRes = Creator.Convert((List<Object>) list, fieldDatas, proxyClass);
        } catch (Exception e) {
            log.error(e);
        }
        //ObservableList <T> ts = FXCollections.observableArrayList ( list );
        tableView.setTableMenuButtonVisible(true);
        //tableView.setTooltip(new Tooltip("Посто таблица"));
        tableView.setStyle("-fx-font: 14px Arial;");


        tableView.setItems(listRes);


        tableView.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {

                }
            });
            return row;
        });
    }

    private void refreshColumnList(TableView table, List<ColumnUserObject> userObjects) {
        userObjects.clear();
        for (Object o : table.getColumns()) {
            TableColumn col = (TableColumn) o;
            ColumnUserObject userObject = (ColumnUserObject) col.getUserData();
            if (userObject != null) {
                userObject.isVisible = col.isVisible();
                userObject.width = col.getWidth();
                userObjects.add(userObject);
            }

        }

    }

    private static <T> void CreateColumns(TableView tableView, List<InnerFieldData> fieldDatas, List<ColumnUserObject> userObjects) {

        boolean valid = isValidateChacheTableState(userObjects, fieldDatas);
        if (valid) {
            for (ColumnUserObject userObject : userObjects) {
                InnerFieldData fieldData = finderFielddata(fieldDatas, userObject.columnName);
                createItemColumn(tableView, userObjects, fieldData);
            }
        } else {
            for (InnerFieldData fieldData : fieldDatas) {
                createItemColumn(tableView, userObjects, fieldData);
            }
        }

    }

    private static InnerFieldData finderFielddata(List<InnerFieldData> fieldDatas, String columnName) {
        for (InnerFieldData fieldData : fieldDatas) {
            if (fieldData.field.getName().equals(columnName)) {
                return fieldData;
            }
        }
        return null;
    }

    private static <T, N> void createItemColumn(TableView tableView, List<ColumnUserObject> userObjects, InnerFieldData fieldData) {

        TableColumn<T, N> column = new TableColumn<>(fieldData.name);
        column.setCellValueFactory(new PropertyValueFactory<>(fieldData.field.getName()));

        if (fieldData.columnCell != null) {
            column.setCellFactory(fieldData.columnCell.getCell());
        } else {


        }


        column.setPrefWidth(fieldData.width);
        {
            ColumnUserObject o = finder(userObjects, fieldData.field.getName());
            if (o == null) {
                o = new ColumnUserObject(column.getWidth(), fieldData.field.getName(), column.isVisible());
                userObjects.add(o);
            } else {
                column.setPrefWidth(o.width);
                column.setVisible(o.isVisible);
            }
            column.setUserData(o);
        }


        tableView.getColumns().add(column);
    }

    private static ColumnUserObject finder(List<ColumnUserObject> userObjects, String name) {
        for (ColumnUserObject userObject : userObjects) {
            if (userObject.columnName.equals(name)) {
                return userObject;
            }
        }
        return null;
    }

    private static boolean isValidateChacheTableState(List<ColumnUserObject> userObjects, List<InnerFieldData> data) {
        if (userObjects.size() == 0 && data.size() == 0) {
            return false;
        }
        if (userObjects.size() != data.size()) {
            return false;
        }

        for (InnerFieldData datum : data) {
            if (finder(userObjects, datum.field.getName()) == null) {
                return false;
            }
        }
        return true;
    }


}