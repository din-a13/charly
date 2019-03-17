package view;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;

import model.*;
import model.inOut.*;
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
     * ______0 SPALTE ______ 1 SPALTE ______ 2 SPALTE ______ 3 SPALTE ______
     * 0 Row __________________aufforderung: Label__________________________ // zentriert mit zeilenumbruch
     * 1 Row __________________auswahl: ComboBox____________________________ //
     * 2 Row ______________anzeige: Label__________.......fileChsBtn: Button // Button ganz rechts
     * 3 Row abbrBtn: Button.........standart: CheckBox:...importBtn: Button // Abstand durch Padding
     *
     */

    public ImportDialog() {

        //
        dialogEingabe = new GridPane();
        dialogEingabe.setHgap(View.SPACE);
        dialogEingabe.setVgap(View.SPACE);
        dialogEingabe.setPadding(View.SPACEAROUND);
        // Testmethode
        // dialogEingabe.setGridLinesVisible(true);

        // // Hinweistext
        Label aufforderung = new Label("Wähle ein vorhandes Projekt aus, oder gib einen Projektnamen ein um ein neuen Projekt zu erzeugen");
        aufforderung.setWrapText(true);
        aufforderung.setTextAlignment(TextAlignment.CENTER);
        aufforderung.setPadding(new Insets(View.SPACE * 3, View.SPACE, View.SPACE * 3, View.SPACE));
        dialogEingabe.add(aufforderung, 0, 0, 4, 1);

        // // ComboBox
        auswahl = new ComboBox<>();
        auswahl.setPromptText("Hier wählen oder tippen");
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
        auswahl.setPrefWidth(Double.MAX_VALUE);
        GridPane.setHgrow(auswahl, Priority.ALWAYS);
        auswahl.setConverter(new StringConverter<Projekt>() {
            @Override
            public String toString(Projekt prj) {
                if (prj != null) {
                    return prj.name();
                } else {
                    return " ";
                }
            }

            @Override
            public Projekt fromString(String eingabe) {
                if (eingabe == null || eingabe.trim().equals("")) { eingabe = "neu"; }
                Projekt neu = Projekt.getProjektStandart(eingabe);
                return neu;
            }
        });
        // // Platzieren
        dialogEingabe.add(auswahl, 0, 1, 3, 1);

        // TODO FileChooser
        // Das ausgewählte Projekt soll in der ComboBox angezeigt werden
        // Das gefundene Projekt wird automatisch in den IO Ordner kopiert
        // Auch Buchungsdatei muss kopiert werden
        Button fileChsBtn = new Button("Datei suchen");
        fileChsBtn.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(fileChsBtn, Priority.ALWAYS);
        GridPane.setHalignment(fileChsBtn, HPos.RIGHT);
        dialogEingabe.add(fileChsBtn, 3, 1);

        // Checkbox
        Label text = new Label("als Standart");
        CheckBox standart = new CheckBox();
        standart.setAllowIndeterminate(false);
        GridPane.setHalignment(text, HPos.RIGHT);
        GridPane.setHalignment(standart, HPos.RIGHT);
        dialogEingabe.add(text, 1, 2);
        dialogEingabe.add(standart, 2, 2);

        // // Buttons
        Button importBtn = new Button("Auswahl laden");
        Button abbrBtn = new Button("Abbrechen");
        abbrBtn.setMaxWidth(Double.MAX_VALUE);
        importBtn.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(importBtn, Priority.ALWAYS);
        GridPane.setHgrow(abbrBtn, Priority.ALWAYS);
        GridPane.setHalignment(abbrBtn, HPos.LEFT);
        GridPane.setHalignment(importBtn, HPos.RIGHT);
        importBtn.setOnAction(er -> presenterImport.returnEnd(auswahl.getValue(), standart.isSelected()));
        abbrBtn.setOnAction(er -> this.close());
        dialogEingabe.add(abbrBtn, 0, 2);
        dialogEingabe.add(importBtn, 3, 2);

        // Columnbreiten
        ColumnConstraints col = new ColumnConstraints(View.EINGABEBREITE * 4);
        dialogEingabe.getColumnConstraints().add(col); // abbrechen
        col = new ColumnConstraints(View.EINGABEBREITE * 3, View.EINGABEBREITE * 6, Double.MAX_VALUE);
        col.setHgrow(Priority.ALWAYS);
        dialogEingabe.getColumnConstraints().add(col); // Label std
        col = new ColumnConstraints(View.EINGABEBREITE * 1);
        dialogEingabe.getColumnConstraints().add(col); // CheckBox
        col = new ColumnConstraints(View.EINGABEBREITE * 5);
        dialogEingabe.getColumnConstraints().add(col); // Button

        // Rowhöhen
        RowConstraints row = new RowConstraints(View.EINGABEBREITE * 2, View.EINGABEBREITE * 3, Double.MAX_VALUE);
        row.setVgrow(Priority.ALWAYS);
        dialogEingabe.getRowConstraints().add(row); // hinweis
        row = new RowConstraints(View.EINGABEBREITE * 2);
        dialogEingabe.getRowConstraints().add(row); // Auswahl

        // Zusammen stecken
        Scene dialogScene = new Scene(dialogEingabe);
        this.setScene(dialogScene);
        this.setMinHeight(View.EINGABEBREITE * 8);
        this.setMinWidth(View.EINGABEBREITE * 15);
        // this.setResizable(false);
        this.setTitle("IMPORT");
        this.initModality(Modality.APPLICATION_MODAL);

        // Focus
        abbrBtn.requestFocus();
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
