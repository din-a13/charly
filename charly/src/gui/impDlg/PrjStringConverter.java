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
        String[] happen = eingabe.split("(");
        String eingabeName = happen[0].trim();
        int eingabeVrs = Integer.parseInt(happen[1].substring(0, happen[1].length()));
        for (Projekt p : auswahl.getItems()) {
            if (p.name().equals(eingabeName) && p.versionNr() == eingabeVrs) { return p; }
        }
        return Projekt.getProjektStandart(eingabe);
    }

    @Override
    public String toString(Projekt prj) {
        if (prj != null) {
            return prj.name() + " (" + prj.versionNr() + ")";
        } else {
            return "";
        }
    }
}
