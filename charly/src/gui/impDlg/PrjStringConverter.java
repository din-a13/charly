package gui.impDlg;

import java.util.regex.*;

import javafx.scene.control.*;
import javafx.util.*;

import model.inOut.*;

public class PrjStringConverter extends StringConverter<Projekt> {
    private ComboBox<Projekt> auswahl;

    public PrjStringConverter(ComboBox<Projekt> auswahl) {
        this.auswahl = auswahl;
    }

    private final String SPLIT = "|";

    @Override
    public Projekt fromString(String eingabe) {
        // Der Nutzer erwartet, wenn keine Eingabe erfolgte, wird abgebrochen
        if (eingabe == null || eingabe.trim().equals("")) { return null; }

        // Entferne Sonderzeichen außer +-_() und |
        String eingabeFlt = eingabe.replaceAll("[^\\wäüöÄÜÖ\\+\\-_\\(\\)\\|]", "");

        // Auswahl des richtigen Projektes aus der Liste
        // Wenn nicht enthalten, dann erzeuge ein neues leeres Projekt mit dem Eingabenamen
        String[] happen = eingabeFlt.split(Pattern.quote(SPLIT));

        // wenn die Länge von Happen nicht passt, dann direkt neues Projekt,
        // dess es wurde offensichtlich händisch eingegeben auch | filtern
        if (happen.length != 2) { return Projekt.getProjektStandart(eingabeFlt.replaceAll("[\\|]", "")); }

        String eingabeName = happen[0].trim();

        int eingabeVrs = Integer.parseInt(happen[1].trim());
        for (Projekt p : auswahl.getItems()) {
            if (p.name().equals(eingabeName) && p.versionNr() == eingabeVrs) { return p; }
        }

        // wenn nix gefunden wurde, dann Standartprojekt
        return Projekt.getProjektStandart(eingabeFlt.replaceAll("[\\|]", ""));
    }

    @Override
    public String toString(Projekt prj) {
        if (prj != null) {
            return prj.name() + "  " + SPLIT + "  " + prj.versionNr();
        } else {
            return "";
        }
    }
}
