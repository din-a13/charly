package gui;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.util.*;

import model.*;

public class BtnCellFactory implements Callback<TableColumn<Buchung, Button>, TableCell<Buchung, Button>> {
    private Image imgBtn;
    private TabTyp tabTyp;
    private Double BREITE;

    // Konstruktor
    public BtnCellFactory(TabTyp tabTyp, Image imgBtn, Double BREITE) {
        this.imgBtn = imgBtn;
        this.tabTyp = tabTyp;
        this.BREITE = BREITE;
    }

    @Override
    public TableCell<Buchung, Button> call(TableColumn<Buchung, Button> param) {
        // inner class
        TableCell<Buchung, Button> cell = new TableCell<Buchung, Button>() {

            // Das hier soll angezeigt werden - ein Button
            private Button changeBtn = new Button();
            {
                ImageView imgBtnV = new ImageView(imgBtn);
                changeBtn.setGraphic(imgBtnV);
                changeBtn.setMinSize(BREITE, BREITE);
                changeBtn.setMaxSize(BREITE, BREITE);
                changeBtn.setOnAction((ActionEvent event) -> {
                    int idxRow = getIndex();
                    Buchung buchung = getTableView().getItems().get(getIndex());
                    tabTyp.changeBuchung(buchung, idxRow);
                });
            }

            @Override
            public void updateItem(Button item, boolean empty) {
                this.setAlignment(Pos.CENTER);
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Prüfung ob es sich nicht um eine Summenzeile handelt
                    // dann darf die nicht änderbar sein
                    Buchung buchung = getTableView().getItems().get(getIndex());
                    if (!buchung.getClass().toString().equals("class model.SummenBuchung")) {
                        setGraphic(changeBtn);
                    } else {
                        setGraphic(null);
                    }

                }
            }
        };
        // Rückgabe 1 Objekt der inner class
        return cell;
    }

}
