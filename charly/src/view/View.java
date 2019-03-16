package view;

import java.util.*;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import presenter.*;

public class View extends HBox {

    /*
     * StyleAtribute - für alle Views Sichtbar
     * __________________________________________________________________
     */
    final static double SPACE = 3.0;
    final static Insets SPACEAROUND = new Insets(SPACE, SPACE, SPACE, SPACE);
    final static double EINGABEBREITE = 20.0;

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

    public View(Presenter presenter, String[] HELDEN, String[] TYPEN) {
        this.presenter = presenter;

        /* TEILUNG hor 2 */
        BorderPane seiteBP = new BorderPane();

        Separator vS = new Separator();
        vS.setOrientation(Orientation.VERTICAL);
        HBox.setHgrow(tabFeld, Priority.ALWAYS); // Tabs sollen mitwachsen
        this.getChildren().addAll(seiteBP, vS, tabFeld);

        // Seitenleiste
        VBox nutzer = new VBox();
        VBox io = new VBox();
        seiteBP.setTop(nutzer);
        seiteBP.setBottom(io);
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
        for (int i = 0; i < HELDEN.length; i++) {
            MenuItem neu = new CheckMenuItem(HELDEN[i]);
            // Listener anmelden
            neu.setOnAction((e) -> heldenWechsel(e, heldenMenu, heldenBild));
            heldenMenu.getItems().add(neu);
        }

        // // Import Export
        Button importieren = new Button("Import");
        Button exportieren = new Button("Export");
        io.getChildren().addAll(importieren, exportieren);
        io.setSpacing(SPACE);
        importieren.setPrefWidth(EINGABEBREITE * 4);
        exportieren.setPrefWidth(EINGABEBREITE * 4);
        // // Listener
        importieren.setOnAction(e -> presenter.Import());
        exportieren.setOnAction(e -> presenter.Export());

        // TabFeld einrichten
        tabFeld.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabFeld.setTabMinWidth(EINGABEBREITE * 4);
        // Tabs einrichten
        for (int j = 0; j < TYPEN.length; j++) {
            TabTyp tab = new TabTyp(presenter, TYPEN[j]);
            // tab.setOnSelectionChanged(e -> presenter.tabellenAnsichtNeu());
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

        // TODO weiteren Tab für Einstellungen

    }

    /*
     * Initialisierung durch Presenter
     * __________________________________________________________________
     */

    // letztes aktives Tab wieder setzen
    public void setTyp(String initTyp) {
        tabFeld.getSelectionModel().select(tabListe.get(initTyp));
    }

    // letzten aktiven Helden setzen
    public void setHeld(String held) {
        // Bild setzen
        heldenBild.setImage(getImage(held));
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

    private Image getImage(String held) {
        String url = "view/" + held + ".bmp";
        Image image = new Image(url, true);
        return image;
    }

    private void heldenWechsel(ActionEvent e, MenuButton heldenMenu, ImageView heldenBild) {
        String aktHeld = ((MenuItem) e.getSource()).getText();
        // Bild
        heldenBild.setImage(getImage(aktHeld));
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
