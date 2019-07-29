package sample.dialog;

import javafx.scene.control.TextInputDialog;
import utils.SettingsApp;
import utils.UtilsOmsk;

import java.util.Optional;

public class DialogEditPointId {

    public static void show(String poin_id){
        TextInputDialog dialog = new TextInputDialog(poin_id);

        dialog.setTitle(null);
        dialog.setHeaderText("Point ID:");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {

            try {
                SettingsApp.setPointId(name);
            } catch (Exception e) {
                MyDialog.showError(e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
