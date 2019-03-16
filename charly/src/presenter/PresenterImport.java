package presenter;

import java.nio.file.*;
import java.util.*;

import javafx.collections.*;

import model.*;
import model.inOut.*;
import view.*;

public class PresenterImport {

    /*
     * Presenter einrichten & View initial aufbauen
     * __________________________________________________________________
     */

    private ImportDialog importDialog;

    private Projekt prj;

    public PresenterImport() {
        // leer
    }

    /*
     * AUFRUFMETHODEN
     * __________________________________________________________________
     */
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
        return prj;
    }

    public Projekt getPrjImport(Projekt wurzelPrj) {
        prj = wurzelPrj;
        // - alle vorhandenen Pfade zu Projektobjekten einlesen
        List<Path> pathList = new ArrayList<>(Datei.prjPathList());
        ObservableList<Projekt> prjList = FXCollections.observableArrayList();
        // wenn keine Projektdateien vorhanden
        // -> aktuelles Wurzelprojekt hinzufügen
        if (pathList.isEmpty()) {
            // Auswahldialog starten mit aktuellem Projekt
            prjList.add(prj);
            importDialog(prjList);
        } else {
            // Projekte erzeugen
            // Auswahldialog starten
            importDialog(prjSammeln(pathList, prjList));
        }
        // die View setzt vor dem Schließen im Presenter das Projekt richtig ein
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
        ImportDialog importDialog = new ImportDialog();
        // View initialisieren
        importDialog.initDialog(this, prjList);
        importDialog.showAndWait();
        // die View setzt vor dem Schließen im Presenter das Projekt richtig ein
    }

    /*
     * Zugriffmethoden für Dialog
     * __________________________________________________________________
     */

    public void returnEnd(Projekt prj, boolean selected) {
        // Bevor das DialogFenster aus showAndWait zurück kehrt, wird im Presenter das Projekt gesetzt
        this.prj = prj;
        // Standart setzen
        if (selected) { Datei.stdPrjDateiSchreiben(prj); }
        // jetzt DialogStage schließen
    }

}
