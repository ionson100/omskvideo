package sample.workers;

import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;
import sample.table.BuilderTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShowmenLogError {
    private static final Logger log = Logger.getLogger(ShowmenLogError.class);
    final List<MError> list = new ArrayList<>();

    public void show(TableView tableView) {


        list.clear();
        try {

            List<String>  lines = Files.readAllLines(Paths.get(System.getProperty("user.home") + File.separator + "omskvideo.log"));
            for (String line : lines) {
            MError er = new MError();
            er.msg = line;
            list.add(er);

            }
            tableView.getItems().clear();
            new BuilderTable().build(list, tableView);
            tableView.scrollTo(tableView.getItems().size() - 1);

        } catch (IOException e1) {
            log.error(e1);
        }

      }

}
