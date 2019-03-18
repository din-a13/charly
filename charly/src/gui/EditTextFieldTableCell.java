package gui;

import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.util.*;

import model.*;

public class EditTextFieldTableCell<S, T> extends TextFieldTableCell<S, T> {

    /***************************************************************************
     * *
     * Constructors *
     * *
     **************************************************************************/

    public EditTextFieldTableCell(TabTyp tab, StringConverter<T> converter) {
        this.getStyleClass().add("text-field-table-cell");
        setConverter(converter);
        this.tab = tab;
    }

    /***************************************************************************
     * *
     * Static cell factories *
     * *
     **************************************************************************/

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(TabTyp tab, final StringConverter<T> converter) {
        return list -> new EditTextFieldTableCell<>(tab, converter);
    }

    /***************************************************************************
     * *
     * Fields *
     * *
     **************************************************************************/

    private TextField textField;

    private TabTyp tab;

    /***************************************************************************
     * *
     * Public API *
     * *
     **************************************************************************/

    // Textfeld für Eingabe erzeugen
    @Override
    public void startEdit() {
        Buchung buchung = (Buchung) getTableView().getItems().get(getIndex());
        if (buchung.getClass().toString().equals("class model.SummenBuchung")) { return; }
        super.startEdit();
    }

    @Override
    public void commitEdit(T newValue) {
        super.commitEdit(newValue);
        tab.aktAnsichtRequest();
    }

    // Vorzeichen - Betrag rot
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {

            // System.out.println(item.getClass());
            // class java.lang.Double
            if (item.getClass().toString().equals("class java.lang.Double")) {
                setStyle("-fx-alignment: TOP-RIGHT;");
                if ((Double) item < 0) { setStyle("-fx-text-fill: #880000;-fx-alignment: TOP-RIGHT;"); }
            }
        }
    }

}
