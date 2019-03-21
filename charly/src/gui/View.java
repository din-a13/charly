package gui;

import java.util.*;

import javafx.event.*;
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

        // // Heldenauswahl
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

        // // Datei Menu
        MenuButton dateiMenu = new MenuButton("Datei");
        dateiMenu.setPrefWidth(EINGABEBREITE * 4);
        dateiMenu.setPopupSide(Side.RIGHT);

        MenuItem imp = new MenuItem("Projekt öffnen");
        imp.setOnAction((e) -> presenter.Import());

        MenuItem buchSpeich = new MenuItem("Buchungen speichern");
        buchSpeich.setOnAction((e) -> presenter.buchungenSpeichern());

        MenuItem exp = new MenuItem("Projekt exportieren");
        exp.setOnAction((e) -> presenter.prjExp());

        dateiMenu.getItems().addAll(imp, new SeparatorMenuItem(), buchSpeich, exp);

        /*
         * TODO nicht mehr gewollt
         * // // Import Export
         * Button importieren = new Button("neu Laden");
         * Button exportieren = new Button("Speichern");
         * importieren.setPrefWidth(EINGABEBREITE * 4);
         * exportieren.setPrefWidth(EINGABEBREITE * 4);
         * // // Listener
         * importieren.setOnAction(e -> presenter.Import());
         * exportieren.setOnAction(e -> presenter.Export());
         */
        setting.getChildren().addAll(dateiMenu);
        setting.setSpacing(SPACE);

        // TabFeld einrichten
        tabFeld.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabFeld.setTabMinWidth(EINGABEBREITE * 4);

    }

    /*
     * Initialisierung durch Presenter
     * __________________________________________________________________
     */
    // Helden löschen und setzen
    void setHelden(String[] HELDEN) {
        heldenMenu.getItems().clear();
        for (int i = 0; i < HELDEN.length; i++) {
            MenuItem neu = new CheckMenuItem(HELDEN[i]);
            // Listener anmelden
            neu.setOnAction((e) -> heldenWechsel(e, heldenMenu, heldenBild));
            heldenMenu.getItems().add(neu);
        }
    }

    // Tab -> Typen löschen und setzen
    void setTypen(String[] TYPEN) {
        // Typen
        tabFeld.getTabs().clear();
        tabListe.clear();
        for (int j = 0; j < TYPEN.length; j++) {
            TabTyp tab = new TabTyp(presenter, TYPEN[j]);
            // brauch ich diese TabListe ??
            // JA, da später auch weitere Tabs kommen könnten
            // z.B. für Einstellungen
            tabListe.put(TYPEN[j], tab);
            tabFeld.getTabs().add(tab);
        }
        // Tab für Auswertung einfügen
        // wird nicht in die tabListe eingefügt, die ist nur für die Typen da
        TabWertung tabA = new TabWertung(presenter, "Auswertung");
        tabFeld.getTabs().add(tabA);
    }

    // letztes aktives Tab wieder setzen
    void setTyp(String initTyp) {
        tabFeld.getSelectionModel().select(tabListe.get(initTyp));
    }

    // letzten aktiven Helden setzen
    void setHeld(String held) {
        // Bild setzen
        heldenBild.setImage(presenter.getImage(held));
        // nur das richtige CheckItem anschalten
        for (MenuItem a : heldenMenu.getItems()) {
            CheckMenuItem b = (CheckMenuItem) a;
            if (b.getText().equals(held)) { b.setSelected(true); }
        }
    }

    /*
     * private Methoden
     * __________________________________________________________________
     */

    private Tab aktTab() {
        return tabFeld.getSelectionModel().getSelectedItem();
    }

    private void heldenWechsel(ActionEvent e, MenuButton heldenMenu, ImageView heldenBild) {
        String aktHeld = ((MenuItem) e.getSource()).getText();
        // Bild
        heldenBild.setImage(presenter.getImage(aktHeld));
        // andere CheckItem
        CheckMenuItem s = (CheckMenuItem) e.getSource();
        for (MenuItem a : heldenMenu.getItems()) {
            CheckMenuItem b = (CheckMenuItem) a;
            if (!b.equals(s)) { b.setSelected(false); }
        }
        // Presenter
        if (true) {
            // TODO Fallunterscheidung, wenn gerade die Einstellungsseite angezeigt wird...
            presenter.heldenWechsel(aktHeld, aktTab());
        }
    }

}
