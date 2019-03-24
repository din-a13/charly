package model;

import java.time.*;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

public class H1Held {

    private NumberBinding heldSumme; // TODO NICHT BENÖTIGT
    private ObservableMap<String, H2Typ> heldTypenMap;
    private ObservableMap<LocalDateTime, NumberBinding> heldKumulDifMap;

    public H1Held(ObservableMap<String, H2Typ> heldTypenMap, ObservableMap<LocalDateTime, NumberBinding> heldKumulDifMap) {
        heldSumme = new SimpleDoubleProperty(0).add(0); // TODO NICHT BENÖTIGT
        this.heldTypenMap = heldTypenMap;
        this.heldKumulDifMap = heldKumulDifMap;
    }

    // _____________________________________________________________
    //
    // Getter Setter
    // _____________________________________________________________

    // TODO NUR ZUR ÜBERPRÜFUNG DES MODELL BENÖTIGT
    // BINDING - ACHTUNG WERTÜBERGABE
    NumberBinding getHeldSumme() {
        return heldSumme;
    }

    void setHeldSumme(NumberBinding heldSumme) {
        this.heldSumme = heldSumme;
    }

    // MAP
    public ObservableMap<String, H2Typ> getHeldTypenMap() {
        return heldTypenMap;
    }

    public ObservableMap<LocalDateTime, NumberBinding> getHeldKumulDifMap() {
        return heldKumulDifMap;
    }
}
