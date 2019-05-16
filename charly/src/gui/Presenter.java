package gui;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;

import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.*;

import gui.impDlg.*;
import inOut.*;
import model.*;

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
    // Projektinstanz & aktHeld über die Wurzel ziehen -> singel source of throuth

    public Presenter(H0Wurzel w) {
        this.wurzel = w;
        this.view = new View(this);
        initView();
    }

    /*
     * View initialisieren & starten
     * __________________________________________________________________
     */

    // initialisieren
    private void initView() {
        String aktHeld = wurzel.getPrj().aktHeld();
        String aktTyp = wurzel.getPrj().aktTyp();
        view.setHelden(wurzel.getPrj().HELDEN(), wurzel.getHeldenXx());
        view.setTypen(wurzel.getPrj().TYPEN(), wurzel.getTypenXx());
        view.setTyp(aktTyp);
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
     * Aktualisieren -- Ansprache durch TabTYP, Presenter
     * __________________________________________________________________
     */

    public void tabellenAnsichtNeu(TabTyp aktTab) {
        wurzel.getPrj().setAktTyp(aktTab.getText());
        ObservableList<Buchung> ganzeListe = wurzel.getTypBuchungsListe();
        ObservableList<Buchung> filterListe = FXCollections.observableArrayList();
        // übernehme keine leeren SummenBuchungen
        for (Buchung b : ganzeListe) {
            if (!(b.getClass().toString().equals("class model.SummenBuchung")) || !(b.getBetrag().get() == 0)) {
                filterListe.add(b);
            }
        }
        aktTab.aktAnsicht(filterListe);
    }

    /*
     * Aktualisieren -- Ansprache durch TabWertung
     * __________________________________________________________________
     */

    public void AuswertungAnsichtNeu(TabWertung source) {
        // MonatSummenWerte aus der Wurzel ziehen:
        // in ein DatenItem schreiben und
        // Je TYP und HELD in eine neue Series einfügen:
        // Parallel max & min ermitteln

        ObservableList<XYChart.Series<String, Number>> seriesListe = FXCollections.observableArrayList();
        Double max = 0.0;
        Double min = 0.0;

        for (int t = 0; t < wurzel.getPrj().TYPEN().length; t++) {
            String typ = wurzel.getPrj().TYPEN()[t];
            for (int h = 0; h < wurzel.getPrj().HELDEN().length; h++) {
                String held = wurzel.getPrj().HELDEN()[h];

                // Datenliste von Wurzel holen
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
     * Aktualisieren -- Ansprache durch TabDiff
     * __________________________________________________________________
     */

    public void DifferenzAnsichtNeu(TabDiff source) {

        ObservableList<XYChart.Series<String, Number>> seriesListe = FXCollections.observableArrayList();

        Double max = 0.0;
        Double min = 0.0;

        for (int h = 0; h < wurzel.getPrj().HELDEN().length; h++) {
            String held = wurzel.getPrj().HELDEN()[h];

            ObservableList<XYChart.Data<String, Number>> datenListe = FXCollections.observableArrayList();

            ObservableMap<LocalDateTime, NumberBinding> kumulDifMap = wurzel.getKumulDifMap(held);
            for (LocalDateTime date : kumulDifMap.keySet()) {

                // Werte sammeln
                String m = datumFormat(date.getYear(), date.getMonthValue());
                double d = kumulDifMap.get(date).doubleValue();
                XYChart.Data<String, Number> datenPunkt = new XYChart.Data<>(m, d);
                datenListe.add(datenPunkt);

                // parallel Max/ Min
                if (d > 0 && d > max) { max = d; }
                if (d < 0 && d < min) { min = d; }

            }

            // neue Series mit Name und Daten erzeugen und hinzufügen
            XYChart.Series<String, Number> series = new Series<>(held + ", " + held, datenListe);
            seriesListe.add(series);
        }

        // Werte an das Tab zurück geben
        source.aktChartDaten(seriesListe);
        source.aktBetragAchse(max, min);
    }

    /*
     * Buchungen -- Ansprache durch TabTYP
     * __________________________________________________________________
     */

    public boolean addBuchung(TabTyp aktTab, String betragString, String hinweis, LocalDate datum) {
        String aktTyp = aktTab.getText();
        try {
            wurzel.add(new Buchung(wurzel.getPrj().aktHeld(), aktTyp, LocalDateTime.of(datum, LocalTime.now()), BetragStringConverter.parseDoubleBetrag(betragString), hinweis));

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        tabellenAnsichtNeu(aktTab);
        return true;
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
        wurzel.getPrj().setAktHeld(aktHeld);
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
        setTitle();
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

    // ExportButton / "Speichern unter"
    public void prjExpChoseName() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichern unter");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Charly-Projekt", "*" + Datei.prjSuffix), new FileChooser.ExtensionFilter("Charly-Buchung", "*" + Datei.bchSuffix), new FileChooser.ExtensionFilter("Charly-Icon", "*.icon.bmp"), new FileChooser.ExtensionFilter("alle Dateien", "*.*"));
        Stage choose = new Stage();
        File dirFile = fileChooser.showSaveDialog(choose);

        if (dirFile != null) {
            Path choosePath = dirFile.toPath();

            // WunschNamen & Pfad extrahieren
            int i = choosePath.getNameCount();
            Path dirPath = choosePath.subpath(0, i - 1);
            System.out.println("dirPath: " + dirPath);

            Path namePath = choosePath.subpath(i - 1, i);
            String dateiname = namePath.toString();
            System.out.println("Dateiname: " + dateiname);
            String[] namenSplitter = dateiname.split(Pattern.quote(Datei.prjSuffix));
            String name = PrjStringConverter.checkedPrjName(namenSplitter[0]);
            System.out.println("Projektname: " + name);
            // Projekt umbenennen
            Datei.changePrjName(wurzel, name);

            // Buchungen und Projekt speichern (dabei ohne increment)
            System.out.println("Ausgewähltes Verzeichniss: " + dirPath);
            Datei.buchExport(wurzel, dirPath);

            // Ansicht erneuern mit neuem Namen
            setTitle();
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
        Path imgPath = Paths.get(prjFolderPath.toString() + "\\" + held + ".icon.bmp");
        try {
            url = imgPath.toUri().toURL().toString();
        } catch (MalformedURLException e) {
            System.out.println("im Projektordner sind keine Icon vorhanden: 'heldName.icon.bmp'");
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
