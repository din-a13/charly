package model;

import java.time.*;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

public class H2Typ {
    private NumberBinding typSumme;
    private ObservableMap<LocalDateTime, Buchung> typenBuchungsMap;

    public H2Typ(ObservableMap<LocalDateTime, Buchung> typenBuchungsMap) {
        typSumme = new SimpleDoubleProperty(0).add(0);
        this.typenBuchungsMap = typenBuchungsMap;
    }

    // _____________________________________________________________
    //
    // Getter
    // _____________________________________________________________

    // BINDING - ACHTUNG WERTÜBERGABE
    public NumberBinding getTypSumme() {
        return typSumme;
    }

    public void setTypSumme(NumberBinding typSumme) {
        this.typSumme = typSumme;
    }

    // MAP
    public ObservableMap<LocalDateTime, Buchung> getTypenBuchungsMap() {
        return typenBuchungsMap;
    }
}
