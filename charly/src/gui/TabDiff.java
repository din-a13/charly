package gui;

import java.time.*;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class TabDiff extends Tab {

    /*
     * View Aufbauen
     * __________________________________________________________________
     */

    // einige Attribute müssen Objektatribute sein,
    // damit Methoden darauf zugreifen können

    private Presenter presenter;
    // chart
    NumberAxis betragAxis;
    CategoryAxis dateAxis;
    StackedAreaChart<String, Number> stackedChart;
    ScrollPane scrollPane;

    public TabDiff(Presenter presenter, String tabName) {
        this.presenter = presenter;
        this.setText(tabName);
        this.setOnSelectionChanged(e -> presenter.DifferenzAnsichtNeu((TabDiff) e.getSource()));

        /* TAB */
        VBox tabInhVbox = new VBox();
        this.setContent(tabInhVbox);

        /*
         * TEILUNG hor 2 START
         */

        // Platzhalter
        HBox platzhalter = new HBox();
        platzhalter.setAlignment(Pos.CENTER);
        platzhalter.setSpacing(View.SPACE * 2);
        Button okBtn = new Button("Platzhalter");
        platzhalter.getChildren().addAll(okBtn);

        // // Axis -> Chart
        // // TODO Dynamisieren an die jeweiligen MAX/ MIN werte anbinden
        betragAxis = new NumberAxis(-10, 1000, 10); // Standartwerte werden später angepasst
        betragAxis.setForceZeroInRange(true); // 0 immer sichtbar
        // betragAxis.setLabel("MonatSummen in €"); // nicht so schön
        betragAxis.setTickUnit(10);
        dateAxis = new CategoryAxis();
        dateAxis.setGapStartAndEnd(true); // Der Monat ist nicht auf der Y-Achse
        stackedChart = new StackedAreaChart<>(dateAxis, betragAxis);

        // // Series einrichten und hinzufügen
        // // Vollständig über Presenter und Aktualisierungsmethode

        // // Chart-ScrollBar
        scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true); // chart wird immer eingepasst in ScrollPane
        VBox.setVgrow(scrollPane, Priority.ALWAYS); // scrollPane wird immer eingepasst in VBox

        scrollPane.setFitToWidth(false); // chart wird am Rand der ScrollPane in der Breite beschnitten
        scrollPane.setPrefWidth(View.EINGABEBREITE * 20); // Bevorzugte Breite
        scrollPane.setMaxWidth(Double.MAX_VALUE);

        scrollPane.setContent(stackedChart);
        initChartWidth();
        jumpToDate(LocalDateTime.now()); // ScrollPane auf dieses Jahr stellen
        presenter.DifferenzAnsichtNeu(this); // zur erstmaligen initialisierung der Achsen nötig

        // TEILUNG hor 2 - ZUSAMMENSETZEN
        tabInhVbox.getChildren().addAll(platzhalter, scrollPane);
        tabInhVbox.setSpacing(View.SPACE * 4);
        tabInhVbox.setPadding(new Insets(View.SPACE * 8, View.SPACE, View.SPACE, 0));

        /*
         * TEILUNG hor 2 ENDE
         */
    }

    /*
     * Chart aktualisieren
     * __________________________________________________________________
     */

    // ChartBreite in Abhängigkeit von den Jahren und ScrollPaneWidth einrichten
    private void initChartWidth() {
        SimpleIntegerProperty jahre = new SimpleIntegerProperty(presenter.ymax() - presenter.ymin() + 1);
        DoubleBinding chartBreite = scrollPane.widthProperty().multiply(jahre);
        stackedChart.prefWidthProperty().bind(chartBreite);
        stackedChart.minWidthProperty().bind(chartBreite);
    }

    private void jumpToDate(LocalDateTime datum) {
        // aY + bY = gesY
        // aY/gesY = HValue
        double gesY = presenter.ymax() - presenter.ymin() + 1;
        double aY = datum.getYear() - presenter.ymin();
        scrollPane.setHvalue(aY / gesY);
    }

    // Betrag Achse anpassen
    public void aktBetragAchse(double max, double min) {
        betragAxis.setUpperBound(max * 2.5);
        if (max > 1000) {
            betragAxis.setTickUnit(50);
        } else {
            if (max > 500) { betragAxis.setTickUnit(20); }
        }
        if (min != 0) { betragAxis.setLowerBound(min * 1.1); }

    }

    // Aktualisierung der Daten
    public void aktChartDaten(ObservableList<Series<String, Number>> seriesListe) {
        stackedChart.getData().setAll(seriesListe);
    }
}
