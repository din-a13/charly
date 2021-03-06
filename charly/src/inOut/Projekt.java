package inOut;

import java.io.*;
import java.nio.file.*;

public class Projekt implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2734922782258655130L;
    // PROJEKT-CLASS
    // durch Serialisierung, zur Datei
    // alle Speicherort spezifischen Daten werden mit statischen Methoden der DateiKlasse während der Laufzeit erzeugt

    // PROJEKTATTRIBUTE
    private String projektName;
    private int versionNr = 0; // erhöht wird nur auf Wunsch - i.d.R. aber min. ein mal!
    private int buchIdx = 0; // vor dem ersten Schreiben wird um 1 erhöht
    private String[] HELDEN;
    private String[] TYPEN;
    private int[] JAHRE = { 0, 0 };

    // SITZUNG
    // Beim lesen aus starddatei wird das gesetzt
    private transient String aktHeld;
    private transient String aktTyp;

    // LAUFZEIT
    // Attribut nur für die Laufzeit - darf nicht serialisiert werden, sonnst Fehler
    // diese wird NACH jedem Lesen aus einer Datei neu gesetzt, sonnst standart gemäß Datei.class
    private transient Path prjFolderPath;

    public Projekt(String projektName, String[] helden, String[] typen, int[] jahre) throws IllegalArgumentException {
        // Wenn kein Path, dann wird standartpfad gestzt
        // dieser wird ggf. noch neu gestzt
        this.prjFolderPath = Datei.stdPrjFolderPath();
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
     * Es muss je ein StandartBild mit Namen Eva.bmp und Daniel.bmp vorhanden sein (sonnst Fehler im Presenter)
     * __________________________________________________________________
     */

    public static Projekt getProjektStandart(String projektName) {
        // DemoArrays
        String[] h = { "Eva", "Daniel" };
        String[] t = { "Lebensmittel", "Kinder", "Wohnung", "Auto", "UrlaubUndSo" };
        int[] j = { 2018, 2030 };
        return new Projekt(projektName, h, t, j);
    }

    public static Projekt getProjektStandart() {
        return getProjektStandart("neu");
    }

    /*
     * Versionskontrolle
     * __________________________________________________________________
     */

    void versionNrIncr() {
        versionNr++;
    }

    void buchIdxIncr() {
        buchIdx++;
    }

    /*
     * Setter - nur durch Datei-Klasse zu nuzten
     * __________________________________________________________________
     */

    // wird nicht serealisiert
    // wenn nicht explizit gesetzt, dann standart gezogen
    void setPrjFolderPath(Path prjFolderPath) {
        this.prjFolderPath = prjFolderPath;
    }

    void setName(String name) {
        this.projektName = name;
        this.versionNr = 0;
        this.buchIdx = 0;
    }

    /*
     * Setter - auch für den Presenter
     * __________________________________________________________________
     */

    // wird nicht serealisiert
    // wenn nicht explizit gesetzt, dann Feld[0]
    public void setAktHeld(String aktHeld) {
        this.aktHeld = aktHeld;
    }

    // wird nicht serealisiert
    // wenn nicht explizit gesetzt, dann Feld[0]
    public void setAktTyp(String aktTyp) {
        this.aktTyp = aktTyp;
    }

    /*
     * Getter
     * __________________________________________________________________
     */
    public String name() {
        return projektName;
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

    public String aktHeld() {
        if (aktHeld == null) { aktHeld = HELDEN[0]; }
        return aktHeld;
    }

    public String aktTyp() {
        if (aktTyp == null) { aktTyp = TYPEN[0]; }
        return aktTyp;
    }

    public int versionNr() {
        return versionNr;
    }

    public int buchIdx() {
        return buchIdx;
    }

    public Path getPrjFolderPath() {
        return prjFolderPath;
    }

}
