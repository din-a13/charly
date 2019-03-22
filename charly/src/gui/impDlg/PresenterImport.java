package gui.impDlg;

import java.io.*;
import java.nio.file.*;

import javafx.stage.*;

import model.*;
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
        // Standartdatei für Projektauswahl suchen
        // gibt null zurück, wenn die Standartdatei nicht auffindbar ist
        // Wenn diese auffindbar ist, wird gelesen
        Path path = Datei.stdPrjPath();
        if (path != null) {
            prj = Datei.readProjekt(path);
            if (prj != null) { return prj; }
        }
        // Wenn die Standartdatei nicht gefunden / gelesen werden konnte:
        // oder das in der Stadartdaei enthalten prj nicht gelesen werden konnte,
        // geht es Richtung Dialog weiter -> wenn keine Projektobjekte da sind
        // eine leere Liste an Auswahldialog übergeben
        viewImportDialog = new ViewImportDialog();
        viewImportDialog.initDialog(this, Datei.prjSammeln());
        viewImportDialog.showAndWait();
        // die View setzt vor dem Schließen im Presenter das Projekt richtig ein
        System.out.println("EINSPRUNG mit : " + prj);
        if (prj == null) { return Projekt.getProjektStandart(); }
        return prj;
    }

    // Aufruf durch PRESENTER
    public Projekt getPrjImport(H0Wurzel wurzel) {
        // Sicherheitshalber wird das aktuelle Projekt exportiert
        // Dadurch taucht es auch in der Auswahlliste des Dialogs auf
        Datei.buchExport(wurzel);

        Projekt altesPrj = wurzel.getPrj();
        prj = altesPrj;
        // alle vorhandenen Pfade zu Projektobjekten einlesen
        // dadurch dass jetzt standartmäßig durch den Presenter gespeicher wird,
        // wird auch das aktuelle Projekt in der Liste auftauchen
        viewImportDialog = new ViewImportDialog();
        viewImportDialog.initDialog(this, Datei.prjSammeln());
        viewImportDialog.showAndWait(); // die View setzt vor dem Schließen im Presenter das Projekt richtig ein
        // EINSPRUNG
        System.out.println("EINSPRUNG mit : " + prj);
        // nur wenn es eine Änderung gab, gibt die Methode etwas != null zurück
        if (prj != altesPrj) { return prj; }
        // sonnst
        return null;
    }

    /*
     * Zugriffmethoden für Dialog
     * __________________________________________________________________
     */

    // nach Beenden des Importdialog wird diese aufgerufen
    void returnEnd(Projekt prj, boolean selected) {
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

    // beim Klicken des "Laden"-Button
    void fileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Projekt suchen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Charly-Projekte", "*.prj.txt"), new FileChooser.ExtensionFilter("Charly-Buchungen", "*.bch.txt"));
        Stage choose = new Stage();
        File file = fileChooser.showOpenDialog(choose);
        if (file != null) {
            Path prjPath = file.toPath();
            // Prüfung ob die Dateiendung stimmt
            if (prjPath != null) {
                System.out.println(prjPath);
                // Projekt erzeugen und hinzufügen
                viewImportDialog.addPrjFile(Datei.readProjekt(prjPath));
                return;
            }
        }
        System.out.println("Ich konnte die Datei nicht als Projekt laden");
    }
}
