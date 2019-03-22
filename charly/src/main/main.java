package main;

import javafx.application.*;
import javafx.stage.*;

import gui.*;
import gui.impDlg.*;
import model.*;
import model.inOut.*;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    H0Wurzel wurzel;
    Projekt prj;

    @Override
    public void start(Stage primaryStage) throws Exception {

        /*
         * Laden der Projektdatei
         * __________________________________________________________________
         */

        PresenterImport presenterImport = new PresenterImport();
        prj = presenterImport.getPrjStart();
        /*
         * Wurzel initieren,
         * Einlesen der verschiedenen Grunddaten (durchWurzel)
         * aus dem Projekt kann später vom Presenter auch der InitHeld erfragt werden
         * __________________________________________________________________
         */
        wurzel = H0Wurzel.getInstance();
        wurzel.initWurzel(prj);
        wurzel.initModel();

        /*
         * Einlesen der Buchungen aus einer Datei, entsprechend Projektpfad
         * __________________________________________________________________
         */

        Datei.buchImport(wurzel);

        /*
         * Presenter initieren
         * __________________________________________________________________
         */

        Presenter presenter = new Presenter(wurzel);
        presenter.showView(primaryStage);

    }

    @Override
    public void stop() {
        /*
         * ?? -> Button überwachen, und fragen, ob gerade eine Datenbankaktion (Wurzel-methode oder IO Aktion) läuft,
         * dann
         * warten
         * - Stop Methode überschreiben:
         * - Letzten User setzen in Projektobjekt
         * - Projektclasse als datei überschreiben
         */

        // TODO Letzten User setzen in Projektobjekt
        // durch Presenter ?
        // TODO Projektclasse als datei überschreiben
        // durch Presenter
        // TODO Standartprojektdateipfad neu setzen

        // Buchungen automatisch schreiben - kein Datenverlust
        // dabei wird auch das Projekt geschrieben, damit BuchIdx richtig abgelegt ist
        Datei.buchExport(wurzel);

        // Projekt schreiben - hier ist letzter User und letzter typ gesetzt
        // TODO BEI TEAMPROJEKTEN MACHT DAS KEINEN SINN
        // DAS MUSS BEIM STANDART GESPEICHERT SEIN

    }

}
