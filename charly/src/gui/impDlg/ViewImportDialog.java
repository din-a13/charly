package gui.impDlg;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

import gui.*;
import model.inOut.*;

public class ViewImportDialog extends Stage {

    /*
     * Dialog einrichten & initial aufbauen
     * __________________________________________________________________
     */

    private PresenterImport presenterImport;
    private GridPane dialogEingabe;
    private ComboBox<Projekt> auswahl;
    private CheckBox standart;

    /*
     * ______0 SPALTE ______ 1 SPALTE ______ 2 SPALTE ______ 3 SPALTE ______
     * 0 Row __________________aufforderung: Label__________________________ // zentriert mit zeilenumbruch
     * 1 Row __________________auswahl: ComboBox____________________________ //
     * 2 Row ______________anzeige: Label__________.......fileChsBtn: Button // Button ganz rechts
     * 3 Row abbrBtn: Button.........standart: CheckBox:...importBtn: Button // Abstand durch Padding
     *
     */

    public ViewImportDialog() {

        //
        dialogEingabe = new GridPane();
        dialogEingabe.setHgap(View.SPACE);
        dialogEingabe.setVgap(View.SPACE);
        dialogEingabe.setPadding(View.SPACEAROUND);
        // Testmethode
        // dialogEingabe.setGridLinesVisible(true);

        // // Hinweistext
        Label aufforderung = new Label(" Wähle ein vorhandes Projekt aus, oder gib einen Projektnamen ein um ein neues Projekt zu erzeugen. (erlaubte Sonderzeichen: +-_()");
        aufforderung.setWrapText(true);
        aufforderung.setTextAlignment(TextAlignment.CENTER);
        aufforderung.setPadding(new Insets(View.SPACE * 3, View.SPACE, View.SPACE * 3, View.SPACE));
        dialogEingabe.add(aufforderung, 0, 0, 4, 1);

        // // ComboBox
        auswahl = new ComboBox<>();
        auswahl.setPromptText("Hier wählen oder tippen");
        PrjStringConverter converter = new PrjStringConverter(auswahl);
        auswahl.setConverter(converter);
        auswahl.setCellFactory(new PrjCellFactory(converter));
        // // Nutzeingabe
        auswahl.setEditable(true);
        auswahl.setPrefWidth(Double.MAX_VALUE);
        GridPane.setHgrow(auswahl, Priority.ALWAYS);

        // // Platzieren
        dialogEingabe.add(auswahl, 0, 1, 3, 1);

        // FileChooser
        Button fileChsBtn = new Button("Projekt suchen");
        fileChsBtn.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(fileChsBtn, Priority.ALWAYS);
        GridPane.setHalignment(fileChsBtn, HPos.RIGHT);
        fileChsBtn.setOnAction(e -> presenterImport.fileChooser());
        dialogEingabe.add(fileChsBtn, 3, 1);

        // Checkbox
        Label text = new Label("als Standart");
        standart = new CheckBox();
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
        this.setMinHeight(View.EINGABEBREITE * 11);
        this.setMinWidth(View.EINGABEBREITE * 16);
        // this.setResizable(false);
        this.setTitle("IMPORT");
        this.initModality(Modality.APPLICATION_MODAL);

        // Focus
        abbrBtn.requestFocus();
    }

    /*
     * Dialog einrichten
     * Die Combobox braucht die Liste, aber der Converter auch - der holt Sie sich über die ComboBox
     * __________________________________________________________________
     */

    void initDialog(PresenterImport presenterImport, ObservableList<Projekt> prjList) {
        this.presenterImport = presenterImport;
        auswahl.setItems(prjList);
    }

    void addPrjFile(Projekt prj) {
        auswahl.getItems().add(prj);
        auswahl.getSelectionModel().select(prj);
        // Damit es beim nächsten Programmstart nicht wieder vergessen ist,
        // soll die Checkbox Standart aktiviert werden
        standart.setSelected(true);
    }

}
