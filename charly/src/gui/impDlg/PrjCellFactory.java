package gui.impDlg;

import javafx.scene.control.*;
import javafx.util.*;

import inOut.*;

public class PrjCellFactory implements Callback<ListView<Projekt>, ListCell<Projekt>> {

    private PrjStringConverter converter;

    public PrjCellFactory(PrjStringConverter converter) {
        super();
        this.converter = converter;
    }

    @Override
    public ListCell<Projekt> call(ListView<Projekt> p) {
        return new ListCell<Projekt>() {
            @Override
            protected void updateItem(Projekt item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    setText(converter.toString(item));
                }
            }
        };
    }
}
