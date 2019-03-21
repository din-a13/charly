package gui.impDlg;

import javafx.scene.control.*;
import javafx.util.*;

import model.inOut.*;

public class PrjCellFactory implements Callback<ListView<Projekt>, ListCell<Projekt>> {

    @Override
    public ListCell<Projekt> call(ListView<Projekt> p) {
        return new ListCell<Projekt>() {
            @Override
            protected void updateItem(Projekt item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(item.name() + " (" + item.versionNr() + ")");
                }
            }
        };
    }
}
