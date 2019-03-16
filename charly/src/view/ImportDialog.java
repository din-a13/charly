package view;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

import model.*;
import presenter.*;

public class ImportDialog extends Stage {

    /*
     * Dialog einrichten & initial aufbauen
     * __________________________________________________________________
     */

    private PresenterImport presenterImport;
    private GridPane dialogEingabe;
    private ComboBox<Projekt> auswahl;

    /*
     * 0 SPALTE ______ 1 SPALTE ______ 2 SPALTE ______ 3 SPALTE
     * __________________aufforderung: Label____________________ // zentriert mit zeilenumbruch
     * __________________auswahl: ComboBox___________________ // in der Eingabezeile erscheint das Projekt aus dem
     * Filechooser
     * ______________anzeige: Label__________.......fileChsBtn: Button // Button ganz rechts, Abstand durch Padding
     * abbrBtn: Button....................standart: CheckBox:...importBtn: Button // Abstand durch Padding
     *
     */

    public ImportDialog() {

        //
        dialogEingabe = new GridPane();
        dialogEingabe.setHgap(View.SPACE);
        dialogEingabe.setVgap(View.SPACE);
        dialogEingabe.setPadding(View.SPACEAROUND);

        // // Hinweistext
        Label aufforderung = new Label("Wähle ein vorhandes Projekt aus, oder gib einen Projektnamen ein um ein neuen Projekt zu erzeugen");
        dialogEingabe.add(aufforderung, 0, 0, 3, 1);

        // // ComboBox
        auswahl = new ComboBox<>();
        auswahl.setCellFactory(new Callback<ListView<Projekt>, ListCell<Projekt>>() {
            @Override
            public ListCell<Projekt> call(ListView<Projekt> p) {
                return new ListCell<Projekt>() {
                    @Override
                    protected void updateItem(Projekt item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.name());
                        }
                    }
                };
            }
        });
        // // Nutzeingabe
        auswahl.setEditable(true);
        // // TODO auswahl.setConverter();
        // // Platzieren
        dialogEingabe.add(auswahl, 0, 1, 3, 1);

        // TODO FileChooser
        // Das ausgewählte Projekt soll in der Anzeige mit Pfad angezeigt werden
        Button fileChsBtn = new Button("Projekt suchen");

        // Anzeige, hier soll das gewählte Projekt noch einmal angezeigt werden,
        // bzw kommt ein Hinweis, wenn ein neues Projekt erzeugt werden soll
        // evt. steht da auch noch einmal der Dateipfad
        Label anzeige = new Label("");
        SimpleStringProperty pfadAnzeige = new SimpleStringProperty("   (lokal) ");
        anzeige.textProperty().bind(auswahl.getValue().nameProperty().concat(pfadAnzeige));
        dialogEingabe.add(anzeige, 0, 4, 2, 1);

        // Checkbox
        CheckBox standart = new CheckBox("Standart");
        standart.setAllowIndeterminate(false);
        dialogEingabe.add(anzeige, 2, 4);

        // // Buttons
        Button importBtn = new Button("Auswahl importieren");
        Button abbrBtn = new Button("Abbrechen");
        importBtn.setOnAction(er -> presenterImport.returnEnd(auswahl.getValue(), standart.isSelected()));
        abbrBtn.setOnAction(er -> this.close());
        dialogEingabe.add(abbrBtn, 0, 5);
        dialogEingabe.add(importBtn, 2, 5);

        // Zusammen stecken
        Scene dialogScene = new Scene(dialogEingabe);
        this.setScene(dialogScene);
        // this.setResizable(false);
        this.setTitle("IMPORT");
        this.initModality(Modality.APPLICATION_MODAL);
    }

    /*
     * Dialog einrichten
     * __________________________________________________________________
     */

    public void initDialog(PresenterImport presenterImport, ObservableList<Projekt> prjList) {
        this.presenterImport = presenterImport;
        auswahl.setItems(prjList);
    }
}
