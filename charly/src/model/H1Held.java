package model;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

public class H1Held {

    private NumberBinding heldSumme;
    private ObservableMap<String, H2Typ> heldTypenMap;

    public H1Held(ObservableMap<String, H2Typ> heldTypenMap) {
        heldSumme = new SimpleDoubleProperty(0).add(0);
        this.heldTypenMap = heldTypenMap;
    }

    // _____________________________________________________________
    //
    // Getter
    // _____________________________________________________________

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
}
