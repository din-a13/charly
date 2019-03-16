package presenter;

import java.time.*;

import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.*;
import javafx.scene.control.*;
import javafx.stage.*;

import model.*;
import view.*;

public class Presenter {

    /*
     * Presenter einrichten & View initial aufbauen
     * __________________________________________________________________
     */
    /*
     * Standartwert setzen - gemäß letzter Nutzung
     *
     */

    private View view;
    private H0Wurzel wurzel;
    // Projektinstanz über die Wurzel ziehen -> singel source of throuth
    String aktHeld;

    public Presenter(H0Wurzel w) {
        this.wurzel = w;
        this.aktHeld = w.getPrj().initHeld();
        this.view = new View(this, w.getPrj().HELDEN(), w.getPrj().TYPEN());
    }

    /*
     * View starten
     * __________________________________________________________________
     */
    public void showView(Stage primaryStage) {
        // View initialisieren
        view.setTyp(wurzel.getPrj().initTyp());
        view.setHeld(aktHeld);
        // Scene und Bühne zusammen stecken & starten
        Scene scene = new Scene(view);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Charly - " + wurzel.getPrj().name());
        primaryStage.show();
    }

    /*
     * GETTER -- Ansprache durch TabTYP
     * __________________________________________________________________
     */

    public int ymin() {
        return wurzel.getPrj().JAHRE()[0];
    }

    public int ymax() {
        return wurzel.getPrj().JAHRE()[1];
    }

    /*
     * Aktualisieren -- Ansprache durch TabTYP, TabWertung, Presenter
     * __________________________________________________________________
     */

    public void tabellenAnsichtNeu(TabTyp aktTab) {
        String aktTyp = aktTab.getText();
        ObservableList<Buchung> ganzeListe = wurzel.getTypBuchungsListe(aktHeld, aktTyp);
        ObservableList<Buchung> filterListe = FXCollections.observableArrayList();
        // übernehme keine leeren SummenBuchungen
        for (Buchung b : ganzeListe) {
            if (!(b.getClass().toString().equals("class model.SummenBuchung")) || !(b.getBetrag().get() == 0)) {
                filterListe.add(b);
            }
        }
        aktTab.aktAnsicht(filterListe);
    }

    public void AuswertungAnsichtNeu(TabWertung source) {
        // Werte aus der Wurzel ziehen:
        // Je TYP und HELD in eine neue Series einfügen:
        // Parallel max & min ermitteln

        ObservableList<XYChart.Series<String, Number>> seriesListe = FXCollections.observableArrayList();
        double max = 0;
        double min = 0;

        for (int t = 0; t < wurzel.getPrj().TYPEN().length; t++) {
            // Typen
            String typ = wurzel.getPrj().TYPEN()[t];
            for (int h = 0; h < wurzel.getPrj().HELDEN().length; h++) {
                // Helden
                String held = wurzel.getPrj().HELDEN()[h];
                // Datenliste
                ObservableList<XYChart.Data<String, Number>> datenListe = FXCollections.observableArrayList();
                // Buchungen holen und DatenPunkt <String,Number> umwandeln
                ObservableList<Buchung> buchList = wurzel.getTypBuchungsListe(held, typ);
                for (Buchung b : buchList) {
                    // Nur MonatsSummenBuchung
                    if (b.getClass().toString().equals("class model.SummenBuchung") && b.getDate().getSecond() == 2) {

                        // Werte sammeln
                        String m = datumFormat(b.getDate().getYear(), b.getDate().getMonthValue());
                        double d = b.getBetrag().get();
                        XYChart.Data<String, Number> datenPunkt = new XYChart.Data<>(m, d);
                        datenListe.add(datenPunkt);

                        // parallel Max/ Min
                        if (d > 0 && d > max) { max = d; }
                        if (d < 0 && d < min) { min = d; }
                    }
                }
                // neue Series mit Name und Daten erzeugen und hinzufügen
                XYChart.Series<String, Number> series = new Series<>(typ + ", " + held, datenListe);
                seriesListe.add(series);
            }
        }
        // Werte an das Tab zurück geben
        source.aktChartDaten(seriesListe);
        source.aktBetragAchse(max, min);
    }

    // Hilfsmethode zur Formatiert der Chart - X-Achse
    private String datumFormat(int y, int m) {
        return y + " - " + m;
    }

    /*
     * Buchungen -- Ansprache durch TabTYP
     * __________________________________________________________________
     */
    public boolean addBuchung(TabTyp aktTab, String betragString, String hinweis, LocalDate datum) {
        String aktTyp = aktTab.getText();
        try {
            wurzel.add(new Buchung(aktHeld, aktTyp, LocalDateTime.of(datum, LocalTime.now()), parseDoubleBetrag(betragString), hinweis));

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        tabellenAnsichtNeu(aktTab);
        return true;
    }

    // Eingabeprüfung für Betrag
    private static Double parseDoubleBetrag(String betragString) {
        // Leerzeichen entfernen
        betragString.trim();
        // wenn leer, dann 0
        if (betragString.equals("")) { return Double.valueOf(0.0); }
        // VORZEICHEN: AUsgaben immer neg. (-)
        char[] betragChrAy = betragString.toCharArray();
        // wenn (-) char=45
        if (betragChrAy[0] == 45) { return -1 * myParser(betragChrAy); }
        // wenn (+) char=43
        if (betragChrAy[0] == 43) { return myParser(betragChrAy); }
        // sonnst immer von neg. Ausgabe ausgehen
        return -1 * myParser(betragChrAy);
    }

    private static Double myParser(char[] betragChrAy) {
        String betragString = "";
        boolean decimal = false;
        // nur Zahlen und das erste Komma/ Punkt werden genommen
        // Rest wird ignoriert
        for (int i = 0; i < betragChrAy.length; i++) {
            if (47 < betragChrAy[i] && betragChrAy[i] < 58) {
                betragString += betragChrAy[i];
            } else {
                if ((betragChrAy[i] == 44 || betragChrAy[i] == 46) & !decimal) {
                    betragString += ".";
                    decimal = true;
                }
            }
        }
        return Double.parseDouble(betragString);
    }

    public boolean removeBuchung(TabTyp aktTab, Buchung buchung) {
        String aktTyp = aktTab.getText();
        try {
            wurzel.remove(buchung);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        tabellenAnsichtNeu(aktTab);
        return true;
    }

    /*
     * Änderung HELD -- Ansprache durch VIEW
     * __________________________________________________________________
     */

    public void heldenWechsel(String aktHeld, Tab aktTab) {
        this.aktHeld = aktHeld;
        if (aktTab.getClass().toString().equals("class view.TabTyp")) {
            String aktTyp = aktTab.getText();
            tabellenAnsichtNeu((TabTyp) aktTab);
            // TODO FALSCH LÖSCHEN ((TabTyp) aktTab).aktAnsicht(wurzel.getTypBuchungsListe(aktHeld, aktTyp));
        }
    }
    /*
     * I / O
     * __________________________________________________________________
     *
     */

    public void Import() {
        PresenterImport presenterImport = new PresenterImport();
        Projekt prj = presenterImport.getPrjImport(wurzel.getPrj());
        // TODO was passiert dann ???

    }

    public void Export() {
        // TODO Auto-generated method stub

    }

    /*
     * EXIT-Verhalten
     * __________________________________________________________________
     *
     */

}
