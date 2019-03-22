package gui;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

import javafx.collections.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.*;

import gui.impDlg.*;
import model.*;
import model.inOut.*;

public class Presenter {

    /*
     * Presenter einrichten & View initial aufbauen
     * __________________________________________________________________
     */
    /*
     * Standartwert setzen - gemäß letzter Nutzung
     *
     */

    private Stage primaryStage;
    private View view;
    private H0Wurzel wurzel;
    // Projektinstanz über die Wurzel ziehen -> singel source of throuth
    String aktHeld;

    public Presenter(H0Wurzel w) {
        this.wurzel = w;
        this.aktHeld = w.getPrj().initHeld();
        this.view = new View(this);
        initView();
    }

    /*
     * View initialisieren & starten
     * __________________________________________________________________
     */

    // initialisieren
    private void initView() {
        view.setHelden(wurzel.getPrj().HELDEN(), wurzel.getHeldenXx());
        view.setTypen(wurzel.getPrj().TYPEN(), wurzel.getTypenXx());
        view.setTyp(wurzel.getPrj().initTyp());
        view.setHeld(aktHeld, getHeldImg(aktHeld));
        setTitle();
    }

    public void showView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Scene und Bühne zusammen stecken & starten
        Scene scene = new Scene(view);
        setTitle();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setTitle() {
        if (primaryStage != null) {

            // Wenn das Projekt noch nie gespeichert wurde, trägt es vrs=0 und idx=0 dann soll aber jeweils "--"
            // angezeigt werden
            String vrs = (wurzel.getPrj().versionNr() == 0) ? "--" : Integer.toString(wurzel.getPrj().versionNr());
            String idx = (wurzel.getPrj().buchIdx() == 0) ? "--" : Integer.toString(wurzel.getPrj().buchIdx());
            primaryStage.setTitle("Charly        ||Projekt:  " + wurzel.getPrj().name() + "  |Version: " + vrs + "  |Index: " + idx);
        }
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

                        // Werte sammeln - mit verkehrtem VORZEICHEN
                        String m = datumFormat(b.getDate().getYear(), b.getDate().getMonthValue());
                        double d = b.getBetrag().get() * -1;
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

    void heldenWechsel(String aktHeld, Tab aktTab) {
        this.aktHeld = aktHeld;
        if (aktTab.getClass().toString().equals("class gui.TabTyp")) {
            String aktTyp = aktTab.getText();
            tabellenAnsichtNeu((TabTyp) aktTab);
        }
    }

    void neuerHeld() {
        TextInputDialog heldNameDlg = new TextInputDialog();
        heldNameDlg.setTitle("neuer Held");
        heldNameDlg.setHeaderText("Gib einen Namen für deinen neuen Helden ein. (erlaubte Sonderzeichen: +-_()");
        Optional<String> result = heldNameDlg.showAndWait();
        result.ifPresent(eingabe -> {
            String eingabeFlt = eingabe.replaceAll("[^\\wäüöÄÜÖ\\+\\-_\\(\\)]", "");
            System.out.println(eingabeFlt);
            // TODO
        });
    }

    public Object heldEntfernen(ActionEvent e) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * Änderung TYP -- Ansprache durch VIEW
     * __________________________________________________________________
     */

    public void neuerTyp() {
        TextInputDialog typNameDlg = new TextInputDialog();
        typNameDlg.setTitle("neue Kategorie");
        typNameDlg.setHeaderText("Gib eine Bezeichnung für die neue Ausgabenkategorie ein. (erlaubte Sonderzeichen: +-_()");
        Optional<String> result = typNameDlg.showAndWait();
        result.ifPresent(eingabe -> {
            String eingabeFlt = eingabe.replaceAll("[^\\wäüöÄÜÖ\\+\\-_\\(\\)]", "");
            // TODO
            System.out.println(eingabeFlt);
        });
    }

    public Object typEntfernen(ActionEvent e) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * I / O Aufrufe durch View
     * __________________________________________________________________
     *
     */
    // ImportButton / "Laden"
    public void Import() {
        PresenterImport presenterImport = new PresenterImport();
        Projekt prj = presenterImport.getPrjImport(wurzel);

        // wenn es keine Änderung gab, kommt null zurück - nichts passiert
        if (prj != null) {
            // WURZEL neu aufbauen
            // MainMethoden nachahmen
            wurzel.initWurzel(prj);
            wurzel.initModel();
            try {
                Datei.buchImport(wurzel);
            } catch (IllegalBuchungException e) {
                System.out.println("Eine oder mehrere Buchungen passen nicht zum Projekt.");
                System.err.print(e.getMessage());
            }
            // View komplett neu initialisieren
            // Das passiert mit dem neuen Projekt in der neuen Wurzel
            initView();
        }
    }

    // ExportButton / "Speichern"
    public void buchungenSpeichern() {
        // Buchungen speichern, ohne increment
        Datei.buchExport(wurzel);
    }

    // ExportButton / "Speichern unter"
    public void prjExp() {
        // benötigt wird ein Verzeichniss zum Speichern
        DirectoryChooser dirChooser = new DirectoryChooser();
        Stage choose = new Stage();
        File dirFile = dirChooser.showDialog(choose);
        if (dirFile != null) {
            Path dirPath = dirFile.toPath();
            System.out.println("Ausgewähltes Verzeichniss: " + dirPath);
            // Buchungen und Projekt speichern (dabei ohne increment)
            Datei.buchExport(wurzel, dirPath);
        }
    }

    void prjNameAndern() {
        TextInputDialog prjNameDlg = new TextInputDialog(wurzel.getPrj().name());
        prjNameDlg.setTitle("Projektname ändern");
        prjNameDlg.setHeaderText("Gib einen neuen Projektnamen ein. (erlaubte Sonderzeichen: +-_()");
        Optional<String> result = prjNameDlg.showAndWait();
        result.ifPresent(eingabe -> {
            String eingabeFlt = eingabe.replaceAll("[^\\wäüöÄÜÖ\\+\\-_\\(\\)]", "");
            Datei.changePrjName(wurzel, eingabeFlt);
        });
        setTitle();
    }

    // Aufruf durch View und init-Methode im Presenter
    Image getHeldImg(String held) {
        // Auffinden der HeldenIcon
        // Verweis auf Standart im View-ordner
        String url = "/gui/" + held + ".bmp";
        // Nur wenn im Projektordener wirklich was drinn ist, wird der Standartverweis überschrieben
        Path prjFolderPath = wurzel.getPrj().getPrjFolderPath();
        Path imgPath = Paths.get(prjFolderPath.toString() + "\\" + held + ".bmp");
        try {
            url = imgPath.toUri().toURL().toString();
        } catch (MalformedURLException e) {
            System.out.println("im Projektordner sind keine Icon vorhanden: 'heldName.bmp'");
        }
        System.out.println("HeldIcon gefunden" + url);
        Image image = new Image(url, true);
        return image;
    }

    /*
     * EXIT-Verhalten
     * __________________________________________________________________
     *
     */

    // TODO wenn das aktuelle Projekt mal als Standart gesetzt war, könnte der Standart auf den Index erhöht werden
}
