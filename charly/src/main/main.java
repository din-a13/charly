package main;

import java.time.*;

import javafx.application.*;
import javafx.beans.binding.*;
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

        Presenter p = new Presenter(wurzel);
        p.showView(primaryStage);

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
        Datei.buchExport(wurzel);
        // Projekt schreiben - hier ist letzter User und letzter typ gesetzt
        // TODO BEI TEAMPROJEKTEN MACHT DAS KEINEN SINN
        // DAS MUSS BEIM STANDART GESPEICHERT SEIN
        Datei.writeProjekt(wurzel.getPrj(), false);

    }

    /*
     * Testmethoden
     * __________________________________________________________________
     */

    private static void testBuchungenLesen(H0Wurzel w) {

        Buchung b = new Buchung("Eva", "Lebensmittel", LocalDateTime.now(), 20.5, "jetzt - alles korrekt");
        try {
            w.add(b);
        } catch (IllegalBuchungException e) {
            e.printStackTrace();
        }

        NumberBinding wurzelSumBind = w.getWurzelSumme();
        System.out.println(wurzelSumBind.getDependencies());
        System.out.println(wurzelSumBind.getValue());
        System.out.println(w.getHeldSumme("Daniel").getValue());
        System.out.println(w.getTypSumme("Daniel", "Lebensmittel").getValue());

        Buchung c = new Buchung("Eva", "Lebensmittel", LocalDateTime.now(), 20.5, "testfehler");
        try {
            w.add(c);
        } catch (IllegalBuchungException e) {
            e.printStackTrace();
        }

        wurzelSumBind = w.getWurzelSumme();
        System.out.println(wurzelSumBind.getDependencies());
        System.out.println(wurzelSumBind.getValue());
        System.out.println(w.getHeldSumme("Eva").getValue());
        System.out.println(w.getTypSumme("Eva", "Lebensmittel").getValue());

        int i = 1;
        for (Buchung e : w.getTypBuchungsListe("Eva", "Lebensmittel")) {
            System.out.println(i++ + " " + e);

        }

    }

}
