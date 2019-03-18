package gui;

import java.time.*;

import javafx.scene.control.*;
import javafx.util.*;

public class DatePickerCellFactory implements Callback<DatePicker, DateCell> {

    // Alle Zellen außerhalb des Projektzeitraums werden ausgegraut
    int min;
    int max;

    DatePickerCellFactory(int min, int max) {
        super();
        this.min = min;
        this.max = max;
    }

    @Override
    public DateCell call(DatePicker param) {
        return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.getYear() < min) {
                    setDisable(true);
                    setTooltip(new Tooltip("zu früh"));
                    setStyle("-fx-background-color: #F5F5F5;-fx-text-fill: #AEAEAE;");
                }
                if (item.getYear() > max) {
                    setDisable(true);
                    setTooltip(new Tooltip("zu spät"));
                    setStyle("-fx-background-color: #F5F5F5;-fx-text-fill: #AEAEAE;");
                }
            }

        };
    }

}
