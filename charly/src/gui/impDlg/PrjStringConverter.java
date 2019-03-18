package gui.impDlg;

import javafx.scene.control.*;
import javafx.util.*;

import model.inOut.*;

public class PrjStringConverter extends StringConverter<Projekt> {
    private ComboBox<Projekt> auswahl;

    public PrjStringConverter(ComboBox<Projekt> auswahl) {
        this.auswahl = auswahl;
    }

    @Override
    public Projekt fromString(String eingabe) {
        // Der Nutzer erwartet, das abgebrochen wird, wenn keine Eingabe erfolgte,
        // und nicht dass ein Standartprojekt angeelgt wird!
        if (eingabe == null || eingabe.trim().equals("")) { return null; }

        // Auwahl des richtigen Projektes aus der Liste
        // Wenn nicht enthalten, dann erzeuge ein neues leeres Projekt mit dem Eingabenamen
        for (Projekt p : auswahl.getItems()) {
            if (p.name().equals(eingabe)) { return p; }
        }
        return Projekt.getProjektStandart(eingabe);
    }

    @Override
    public String toString(Projekt prj) {
        if (prj != null) {
            return prj.name();
        } else {
            return "";
        }
    }
}
