package gui.impDlg;

import java.nio.file.*;
import java.util.*;

import javafx.collections.*;

import model.inOut.*;

public class PresenterImport {

    /*
     * Presenter einrichten & View initial aufbauen
     * __________________________________________________________________
     */

    private ViewImportDialog viewImportDialog;
    private Projekt prj;
    boolean prjHasChanged;

    public PresenterImport() {
        prjHasChanged = false;
    }

    /*
     * AUFRUFMETHODEN
     * __________________________________________________________________
     */
    // Aufruf durch MAIN
    public Projekt getPrjStart() {
        prj = null;
        // - Standartdatei für Projektauswahl autom. laden
        Path path = Datei.stdPrjPath(); // gibt null zurück, wenn keine Std gesetzt ist
        // Deserialisierung, wenn nicht null
        if (path != null) {
            prj = Datei.readProjekt(path);
            if (prj == null) { prj = Projekt.getProjektStandart(); }
        } else {
            // Wenn kein Standart gespeichert ist, oder nicht auffindbar (null):
            // - alle vorhandenen Pfade zu Projektobjekten einlesen
            List<Path> pathList = new ArrayList<>(Datei.prjPathList());
            ObservableList<Projekt> prjList = FXCollections.observableArrayList();
            // wenn keine Projektdateien vorhanden -> Standartprojekt
            if (pathList.isEmpty()) {
                prj = Projekt.getProjektStandart();
            } else {
                // Projekte erzeugen
                // Auswahldialog starten
                importDialog(prjSammeln(pathList, prjList));
            }
        }
        // die View setzt vor dem Schließen im Presenter das Projekt richtig ein
        // EINSPRUNG
        System.out.println("EINSPRUNG");
        return prj;
    }

    // Aufruf durch Presenter
    public Projekt getPrjImport(Projekt wurzelPrj) {
        prj = wurzelPrj;
        // - alle vorhandenen Pfade zu Projektobjekten einlesen
        List<Path> pathList = new ArrayList<>(Datei.prjPathList());
        ObservableList<Projekt> prjList = FXCollections.observableArrayList();
        // wenn keine Projektdateien vorhanden
        // -> aktuelles Wurzelprojekt hinzufügen
        if (pathList.isEmpty()) {
            // Auswahldialog starten mit aktuellem Projekt
            // TODO prüfen - das erwartet der Nutzer eigendlich nicht
            prjList.add(prj);

            importDialog(prjList);
        } else {
            // muss hier auch das aktuelle Projekt angezeigt werden ?
            // nein - das erwartet der User nicht beim Knopf neu laden
            // TODO - prüfen, dadurch dass jetzt standartmäßig gespeicher wird, iwrd auch das aktuelle Projekt angezeigt

            // Projekte erzeugen & Auswahldialog starten
            importDialog(prjSammeln(pathList, prjList));
        }
        // die View setzt vor dem Schließen im Presenter das Projekt richtig ein
        // EINSPRUNG
        System.out.println("EINSPRUNG");
        return prj;
    }

    /*
     * private Hilfs METHODEN
     * __________________________________________________________________
     */

    // PROJEKTE einlesen
    private ObservableList<Projekt> prjSammeln(List<Path> pathList, ObservableList<Projekt> prjList) {
        for (Path pth : pathList) {
            Projekt pr = null;
            pr = Datei.readProjekt(pth);
            if (pr != null) { prjList.add(pr); }
        }
        return prjList;
    }

    // DIALOG STARTEN
    private void importDialog(ObservableList<Projekt> prjList) {
        // Auswahldialog starten
        viewImportDialog = new ViewImportDialog();
        // View initialisieren
        viewImportDialog.initDialog(this, prjList);
        viewImportDialog.showAndWait();
        // die View setzt vor dem Schließen im Presenter das Projekt richtig ein
    }

    /*
     * Zugriffmethoden für DialogEnde
     * __________________________________________________________________
     */

    public void returnEnd(Projekt prj, boolean selected) {
        // Bevor das DialogFenster aus showAndWait zurück kehrt, wird hier im Presenter das Projekt gesetzt
        // Test, ob sich was überhaupt was geädert hat
        if (this.prj != prj && prj != null) {
            this.prj = prj;
            this.prjHasChanged = true;
        }
        // Standart setzen
        if (selected && prj != null) { Datei.stdPrjDateiSchreiben(prj); }
        // jetzt DialogStage schließen
        viewImportDialog.close();
        // Jetzt springt der Focus zurück zum Punkt showAndWait
    }

    /*
     * Zugriffmethoden für Presenter
     * __________________________________________________________________
     */
    public boolean prjHasChanged() {
        return prjHasChanged;
    }

}
