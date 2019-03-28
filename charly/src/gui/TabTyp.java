package gui;

import java.text.*;
import java.time.*;
import java.util.*;

import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.converter.*;

import model.*;

public class TabTyp extends Tab {

    /*
     * View Aufbauen
     * __________________________________________________________________
     */

    // einige Attribute müssen Objektatribute sein,
    // damit Methoden darauf zugreifen können

    private Presenter presenter;

    // Tabelle
    private TableView<Buchung> buchungsTabelle = new TableView<>();

    // Eingabeelemente
    private DatePicker datumE = new DatePicker();
    private TextField betragE = new TextField();
    private TextField hinweisE = new TextField();
    Button okBtn = new Button("OK");

    public TabTyp(Presenter presenter, String typ) {
        this.presenter = presenter;
        this.setText(typ);
        this.setOnSelectionChanged(e -> presenter.tabellenAnsichtNeu((TabTyp) e.getSource()));

        /* TAB */
        VBox tabInhVbox = new VBox();
        this.setContent(tabInhVbox);

        /* TEILUNG hor 2 */
        HBox eingabeHbox = new HBox();
        tabInhVbox.getChildren().addAll(eingabeHbox, buchungsTabelle);
        tabInhVbox.setSpacing(View.SPACE * 4);
        tabInhVbox.setPadding(new Insets(View.SPACE * 8, View.SPACE, View.SPACE, 0));

        // Eingabezeile
        eingabeHbox.getChildren().addAll(datumE, betragE, hinweisE, okBtn);
        eingabeHbox.setAlignment(Pos.CENTER);
        eingabeHbox.setSpacing(View.SPACE * 2);

        // // DatePicker einrichten
        datumE.setValue(LocalDate.now());
        datumE.setDayCellFactory(new DatePickerCellFactory(presenter.ymin(), presenter.ymax()));
        datumE.setPrefWidth(View.EINGABEBREITE * 6);
        // // Eingabeelemente einrichten
        betragE.setPrefWidth(View.EINGABEBREITE * 4);
        hinweisE.setPrefWidth(View.EINGABEBREITE * 10);
        betragE.setPromptText("Betrag");
        hinweisE.setPromptText("Hinweis");
        // // Eventhandler: zum nächsten springen / bestätigen
        datumE.setOnAction(e -> eingabeBestaetigen(e));
        betragE.setOnAction(e -> eingabeBestaetigen(e));
        hinweisE.setOnAction(e -> eingabeBestaetigen(e));
        okBtn.setOnAction(e -> eingabeBestaetigen(e));
        okBtn.setDefaultButton(true);

        // // TabellenElemente
        TableColumn<Buchung, String> datumCol = new TableColumn<>("Datum");
        TableColumn<Buchung, Number> betragCol = new TableColumn<>("Betrag");
        TableColumn<Buchung, String> hinweisCol = new TableColumn<>("Hinweis");
        TableColumn<Buchung, Button> btnCol = new TableColumn<>();

        // // // Collums vorbereiten
        buchungsTabelle.setEditable(true);
        buchungsTabelle.setPrefSize(View.EINGABEBREITE * 24, View.EINGABEBREITE * 40);
        buchungsTabelle.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Die Col sollen immer die Breite
                                                                                    // ausfüllen
        datumCol.setMinWidth(View.EINGABEBREITE * 5);
        datumCol.setSortable(false);
        datumCol.setStyle("-fx-alignment: TOP-CENTER;");
        betragCol.setMinWidth(View.EINGABEBREITE * 4);
        betragCol.setSortable(false);
        hinweisCol.setMinWidth(View.EINGABEBREITE * 8);
        hinweisCol.setSortable(false);
        hinweisCol.setMaxWidth(Double.MAX_VALUE); // Diese Spalte Soll mitwachsen
        datumCol.setCellValueFactory(new DateCellValueFactory());
        // Format Betrag
        // TODO ich muss einen eigenen betrag Converter für die String->Double conversion haben!
        NumberFormat betragFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        NumberStringConverter betragConverter = new BetragStringConverter(betragFormat);
        betragCol.setCellValueFactory(item -> item.getValue().getBetrag());
        betragCol.setCellFactory(EditTextFieldTableCell.<Buchung, Number> forTableColumn(this, betragConverter));
        hinweisCol.setCellValueFactory(item -> item.getValue().getHinweis());
        hinweisCol.setCellFactory(EditTextFieldTableCell.<Buchung, String> forTableColumn(this, new DefaultStringConverter()));

        // // // Collum Lösch & ÄnderungsButton
        Image imgChangeBuchungCol = new Image("gui/changeBuchung.png", 14, 14, false, false, true);
        ImageView iconChangeBuchungCol = new ImageView();
        iconChangeBuchungCol.setImage(imgChangeBuchungCol);
        btnCol.setGraphic(iconChangeBuchungCol);
        btnCol.setMaxWidth(View.SPACE * 8);
        btnCol.setMinWidth(View.SPACE * 8);
        btnCol.setSortable(false);
        Image imgChangeBuchungBtn = new Image("gui/deleteBuchung.png", 7, 7, false, false, true);
        btnCol.setCellFactory(new BtnCellFactory(this, imgChangeBuchungBtn, View.SPACE * 5)); // kann Die Größe des
                                                                                              // Button anpassen
        // // // Tabelle zusammenstecken
        buchungsTabelle.getColumns().add(datumCol);
        buchungsTabelle.getColumns().add(betragCol);
        buchungsTabelle.getColumns().add(hinweisCol);
        buchungsTabelle.getColumns().add(btnCol);

    }

    /*
     * Tabelle aktualisieren
     * __________________________________________________________________
     */

    // Aktualisierung anfordern
    public void aktAnsichtRequest() {
        presenter.tabellenAnsichtNeu(this);
    }

    // Aktualisierung ausführen
    public void aktAnsicht(ObservableList<Buchung> anzeige) {
        buchungsTabelle.setItems(anzeige);
        // Textfelder leeren

        betragE.setText("");
        hinweisE.setText("");

        // Focus setzen
        betragE.requestFocus();
    }

    /*
     * Buchung erzeugen - Methoden
     * __________________________________________________________________
     */

    private void eingabeBestaetigen(ActionEvent e) {
        if (e.getSource() == datumE) {
            betragE.requestFocus();
            return;
        }
        if (e.getSource() == betragE) {
            hinweisE.requestFocus();
            return;
        }
        if (e.getSource() == hinweisE) {
            okBtn.requestFocus();
            return;
        }
        if (e.getSource() == okBtn) {
            // Neue / geänderte Buchung an das Modell übergeben
            // Werte aus den Feldern holen
            LocalDate datum = datumE.getValue();
            String betragString = betragE.getText();
            String hinweis = hinweisE.getText();
            try {
                if (presenter.addBuchung(this, betragString, hinweis, datum)) {
                    // Tabelle aktualisieren über Presenter
                    aktAnsichtRequest();
                    return;
                } else {
                    alarm("Fehler", "Wert wurde nicht abgelegt");
                    return;
                }
            } catch (Exception e2) {
                alarm("Eingabefehler", "Deine Eingabe war ungültig");
                return;
            }
        }
    }

    /*
     * Ändern/ LÖSCHEN von bestehenden Einträgen
     * __________________________________________________________________
     */

    public void changeBuchung(Buchung buchung, int idxRow) {
        // Werte aus Buchung auslesen und zwischenspeichern
        LocalDate d = buchung.getDate().toLocalDate();
        String b = Double.toString(buchung.getBetrag().getValue());
        String h = buchung.getHinweis().getValue();

        // Buchung aus dem Datenmodell entfernen
        if (!presenter.removeBuchung(this, buchung)) {
            alarm("Fehler", "Wert wurde nicht entfernt");
            return;
        }
        aktAnsichtRequest();
        // Neue Buchung mit geänderten Werten hinzufügen
        // dann mit der Eingabe über Textfelder und OK-Button

        // Werte aus Buchung Textlabel anzeigen
        datumE.setValue(d);
        betragE.setText(b);
        hinweisE.setText(h);
    }

    /*
     * ALARM POP-UP
     * __________________________________________________________________
     */

    private void alarm(String titel, String note) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titel);
        alert.setHeaderText(null);
        alert.setContentText(note);
        alert.showAndWait();
    }
}
