package main;

import java.time.*;

import javafx.application.*;
import javafx.beans.binding.*;
import javafx.stage.*;

import model.*;
import model.inOut.*;
import presenter.*;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    H0Wurzel w;
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
         * __________________________________________________________________
         */
        w = H0Wurzel.getInstance();
        w.initWurzel(prj);
        w.initModel();

        /*
         * TODO Einlesen der Sitzungsdaten aus einer Datei
         * __________________________________________________________________
         */

        /*
         * TODO Einlesen der Buchungen aus einer Datei
         * __________________________________________________________________
         */

        Datei.buchImport(w, prj);

        /*
         * Presenter initieren
         * __________________________________________________________________
         */

        // TODO
        Presenter p = new Presenter(w);
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

        // testmethode
        Datei.buchExport(w, prj);
        // testmethode
        Datei.writeProjekt(prj);
        // testmethode
        Datei.stdPrjDateiSchreiben(prj);

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
