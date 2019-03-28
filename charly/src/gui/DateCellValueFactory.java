package gui;

import java.time.*;
import java.time.format.*;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.TableColumn.*;
import javafx.util.*;

import model.*;

class DateCellValueFactory implements Callback<CellDataFeatures<Buchung, String>, ObservableValue<String>> {

    @Override
    public ObservableValue<String> call(CellDataFeatures<Buchung, String> buchungZeile) {
        Buchung buchung = buchungZeile.getValue();
        LocalDateTime date = buchung.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd'.'MM'.'yyyy");
        String s = date.format(formatter);
        return new SimpleStringProperty(s);
    }

}
