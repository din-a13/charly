package gui;

import java.util.*;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class View extends HBox {

    /*
     * StyleAtribute - für alle Views Sichtbar
     * __________________________________________________________________
     */
    public final static double SPACE = 3.0;
    public final static Insets SPACEAROUND = new Insets(SPACE, SPACE, SPACE, SPACE);
    public final static double EINGABEBREITE = 20.0;

    /*
     * View Aufbauen
     * __________________________________________________________________
     */

    // einige Attribute müssen Objektatribute sein,
    // damit Methoden darauf zugreifen können

    private Presenter presenter;

    // Abfrage-Indikator für den aktuellen Tab / Typ
    TabPane tabFeld = new TabPane();
    // Sammlung der vorhandenn Tabs zum setzen des letzten Tab genutzt
    HashMap<String, TabTyp> tabListe = new HashMap<>();
    // Heldenauswahl
    ImageView heldenBild;
    MenuButton heldenMenu;
    ToggleGroup heldenToggle;
    // AnpassenMenu
    Menu heldXxMenue;
    Menu typXxMenue;

    public View(Presenter presenter) {
        this.presenter = presenter;

        /* TEILUNG hor 2 */
        BorderPane seiteBP = new BorderPane();

        Separator vS = new Separator();
        vS.setOrientation(Orientation.VERTICAL);
        HBox.setHgrow(tabFeld, Priority.ALWAYS); // Tabs sollen mitwachsen
        this.getChildren().addAll(seiteBP, vS, tabFeld);

        // Seitenleiste
        VBox nutzer = new VBox();
        VBox setting = new VBox();
        seiteBP.setTop(nutzer);
        seiteBP.setBottom(setting);
        seiteBP.setPadding(SPACEAROUND);

        // // Heldenauswahlfeld
        heldenBild = new ImageView();
        Separator hS = new Separator();
        heldenMenu = new MenuButton("Held");
        nutzer.getChildren().addAll(heldenBild, hS, heldenMenu);
        nutzer.setSpacing(SPACE);
        // // // Bild - resizes while preserving the ratio and using
        // // // higher quality filtering method
        // // // ImageView also cached to improve performance
        heldenBild.setFitWidth(EINGABEBREITE * 4);
        heldenBild.setPreserveRatio(true);
        heldenBild.setSmooth(true);
        heldenBild.setCache(true);

        // // // Helden Menu
        heldenMenu.setPrefWidth(EINGABEBREITE * 4);
        heldenMenu.setPopupSide(Side.RIGHT);
        heldenToggle = new ToggleGroup();
        heldenToggle.selectedToggleProperty().addListener((ov, oldToggle, newToggle) -> heldenWechsel(newToggle));

        // // Datei Menu
        MenuButton dateiMenu = new MenuButton("Datei");
        dateiMenu.setPrefWidth(EINGABEBREITE * 4);
        dateiMenu.setPopupSide(Side.RIGHT);
        MenuItem imp = new MenuItem("Projekt öffnen");
        imp.setOnAction((e) -> presenter.Import());
        MenuItem buchSpeich = new MenuItem("Projekt speichern");
        buchSpeich.setOnAction((e) -> presenter.buchungenSpeichern());
        MenuItem exp = new MenuItem("Projekt exportieren");
        exp.setOnAction((e) -> presenter.prjExpChoseName());
        dateiMenu.setPopupSide(Side.RIGHT);
        dateiMenu.getItems().addAll(imp, new SeparatorMenuItem(), buchSpeich, exp);

        // // AnpassenMenu
        MenuButton anpassenMenu = new MenuButton("Projekt");
        anpassenMenu.setPrefWidth(EINGABEBREITE * 4);
        anpassenMenu.setPopupSide(Side.RIGHT);
        MenuItem nameAndern = new MenuItem("Projektname ändern");
        nameAndern.setOnAction((e) -> presenter.prjNameAndern());
        MenuItem neuerHeld = new MenuItem("neuer Held");
        neuerHeld.setOnAction((e) -> presenter.neuerHeld());
        heldXxMenue = new Menu("leere Helden löschen");
        MenuItem neuerTyp = new MenuItem("neuer Tab");
        neuerTyp.setOnAction((e) -> presenter.neuerTyp());
        typXxMenue = new Menu("leere Tabs löschen");
        anpassenMenu.getItems().addAll(nameAndern, new SeparatorMenuItem(), neuerHeld, heldXxMenue, new SeparatorMenuItem(), neuerTyp, typXxMenue);

        // // Setting Bereich füllen
        setting.getChildren().addAll(dateiMenu, anpassenMenu);
        setting.setSpacing(SPACE);

        // TabFeld einrichten
        tabFeld.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabFeld.setTabMinWidth(EINGABEBREITE * 4);
    }

    /*
     * Initialisierung durch Presenter
     * __________________________________________________________________
     */
    // Helden und HeldenXx löschen und setzen
    void setHelden(String[] HELDEN, String[] heldenXx) {
        heldenMenu.getItems().clear();
        // heldenToggle.getToggles().clear();
        for (int i = 0; i < HELDEN.length; i++) {
            RadioMenuItem neu = new RadioMenuItem(HELDEN[i]);
            neu.setToggleGroup(heldenToggle);
            heldenMenu.getItems().add(neu);
        }
        heldXxMenue.getItems().clear();
        for (int i = 0; i < heldenXx.length; i++) {
            MenuItem neuXx = new MenuItem("Xx " + heldenXx[i]);
            neuXx.setOnAction((e) -> presenter.heldEntfernen(e));
            heldXxMenue.getItems().add(neuXx);
        }
    }

    // Tab -> Typen löschen und setzen
    void setTypen(String[] TYPEN, String[] typenXX) {
        tabFeld.getTabs().clear();
        tabListe.clear();
        for (int j = 0; j < TYPEN.length; j++) {
            TabTyp tab = new TabTyp(presenter, TYPEN[j]);
            tabListe.put(TYPEN[j], tab);
            tabFeld.getTabs().add(tab);
        }
        // Tab für Auswertung einfügen
        // wird nicht in die tabListe eingefügt, die ist nur für die Typen da
        TabWertung tabA = new TabWertung(presenter, "Auswertung");
        tabFeld.getTabs().add(tabA);

        // Tab für Differenzansicht einfügen
        // wird nicht in die tabListe eingefügt, die ist nur für die Typen da
        TabDiff tabD = new TabDiff(presenter, "Differenz");
        tabFeld.getTabs().add(tabD);

        // Typenbearbeitungsmöglichkeit (Menü) aktualisieren
        typXxMenue.getItems().clear();
        for (int i = 0; i < typenXX.length; i++) {
            MenuItem neuXx = new MenuItem("Xx " + typenXX[i]);
            neuXx.setOnAction((e) -> presenter.typEntfernen(e));
            typXxMenue.getItems().add(neuXx);
        }
    }

    // letztes aktives Tab wieder setzen
    void setTyp(String initTyp) {
        tabFeld.getSelectionModel().select(tabListe.get(initTyp));
    }

    // Held setzen
    void setHeld(String held, Image image) {
        heldenBild.setImage(image);
        for (Toggle a : heldenToggle.getToggles()) {
            if (((RadioMenuItem) a).getText().equals(held)) { heldenToggle.selectToggle(a); }
        }
    }

    /*
     * private Methoden
     * __________________________________________________________________
     */

    private Tab aktTab() {
        return tabFeld.getSelectionModel().getSelectedItem();
    }

    private void heldenWechsel(Toggle newToggle) {
        String aktHeld = ((RadioMenuItem) newToggle).getText();
        heldenBild.setImage(presenter.getHeldImg(aktHeld));
        presenter.heldenWechsel(aktHeld, aktTab());
    }

}
