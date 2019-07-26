package sample.dialog;

import javafx.scene.control.Alert;

public class MyDialog {
    public static void showError(String s){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText( s);
            alert.showAndWait();
    }

    public static void showInfo(String s){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText( s);
        alert.showAndWait();
    }

    public static void showWarning(String s){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText( s);
        alert.showAndWait();
    }
}
