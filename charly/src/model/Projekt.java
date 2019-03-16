package model;

import java.io.*;

import javafx.beans.property.*;

public class Projekt implements Serializable {

    // PROJEKT-CLASS
    // - durch Serialisierung, zur Datei
    // - Dateiname erste 6 Zeichen des Projektname, wenn schon vorhanden, dann n+1
    // - Dateiendung:prj
    // - Projektname = String
    // - eigener Dateiname = String ( muss beim Öffnen überschrieben werden, falls es Änderungen durch den WinExplorer
    // gab)
    // - ARRAYS: HELDEN TYPEN JAHRE
    // - letzter Nutzer

    // PROJEKTATTRIBUTE
    private String projektName;
    private int versionNr = 1;
    private int buchIdx = 1;
    private String[] HELDEN;
    private String[] TYPEN;
    private int[] JAHRE = { 0, 0 };

    // SITZUNG
    private String initHeld;
    private String initTyp;

    public Projekt(String projektName, String[] helden, String[] typen, int[] jahre) throws IllegalArgumentException {
        // NULL test
        if (projektName == null || helden == null || typen == null || jahre == null) {
            throw new IllegalArgumentException("ARGUMENTE DÜRFEN NICHT NULL SEIN");
        }
        // Projektname
        this.projektName = projektName;
        // Werte werden zuerst überprüft
        // helden
        for (int i = 0; i < helden.length - 1; i++) {
            for (int j = i + 1; j < helden.length; j++) {
                if (helden[i].equals(helden[j])) {
                    throw new IllegalArgumentException("ES KANN NUR EINEN GEBEN! - Helden bitte nicht doppeln");
                }
            }
        }
        this.HELDEN = helden;

        // typen
        for (int i = 0; i < typen.length - 1; i++) {
            for (int j = i + 1; j < typen.length; j++) {
                if (typen[i].equals(typen[j])) {
                    throw new IllegalArgumentException("ES KANN NUR EINEN GEBEN! - Typen bitte nicht doppeln");
                }
            }
        }
        this.TYPEN = typen;

        // Jahre - wenn das Feld 3 lang ist, kommt es aus einer Importfunktion und muss nicht geprüft werden
        if (jahre[0] < 2000 || 2099 < jahre[1] || jahre[0] > jahre[1]) {
            throw new IllegalArgumentException("WANN LEBST DU DENN? - bitte 2000<=min, max<=2099");
        }
        this.JAHRE[0] = jahre[0];
        this.JAHRE[1] = jahre[1];
    }

    /*
     * Standart leer
     * __________________________________________________________________
     */
    public static Projekt getProjektStandart() {
        // DemoArrays
        String[] h = { "Eva", "Daniel" };
        String[] t = { "Lebensmittel", "Kinder", "Wohnung", "Auto", "UrlaubUndSo" };
        int[] j = { 2019, 2020 };
        return new Projekt("neu", h, t, j);
    }

    /*
     * Versionskontrolle
     * __________________________________________________________________
     */

    public void versionNrIncr() {
        versionNr++;
    }

    public void buchIdxIncr() {
        buchIdx++;
    }

    /*
     * Getter
     * __________________________________________________________________
     */
    public String name() {
        return projektName;
    }

    public SimpleStringProperty nameProperty() {
        return new SimpleStringProperty(projektName);
    }

    public String[] HELDEN() {
        return HELDEN;
    }

    public String[] TYPEN() {
        return TYPEN;
    }

    public int[] JAHRE() {
        return JAHRE;
    }

    public String initHeld() {
        if (initHeld == null) { initHeld = HELDEN[0]; }
        return initHeld;
    }

    public String initTyp() {
        if (initTyp == null) { initTyp = TYPEN[0]; }
        return initTyp;
    }

    public int versionNr() {
        return versionNr;
    }

    public int buchIdx() {
        return buchIdx;
    }

}
