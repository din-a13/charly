package model;

import java.time.*;
import java.util.*;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;

import model.inOut.*;

// der oberste Knoten des Modells über ihn läuft der gesamte Buchungsprozess
public class H0Wurzel {

    /*
     * Singelton
     * __________________________________________________________________
     */
    // Eine (versteckte) Klassenvariable vom Typ der eigenen Klasse
    private static H0Wurzel instance;

    // Verhindere die Erzeugung des Objektes über andere Methoden
    private H0Wurzel() {
        // leerer Konstruktor
    }

    // Zugriffsmethode auf Klassenebene, welches einmal ein konkretes
    // Objekt erzeugt und dieses zurückliefert.
    public static synchronized H0Wurzel getInstance() {
        if (H0Wurzel.instance == null) { H0Wurzel.instance = new H0Wurzel(); }
        return H0Wurzel.instance;
    }

    /*
     * Wurzel initieren
     * __________________________________________________________________
     */

    Projekt prj;

    public void initWurzel(Projekt project) {
        this.prj = project;
    }

    /*
     * Modell aufbauen
     * __________________________________________________________________
     */

    /*
     * DEFINITION
     *
     * SUMMEN - Lösung mit SimpleDoubleProperty
     * an diese werden die DoubleProperties der Buchungen gebunden
     *
     * // wurzelSumme 1 ____________________________________________ Binding
     * // // heldSumme 1...n Wurzel ________________________________ Binding
     * // // // typSumme 1...nWurzel _______________________________ Binding
     * // // // // jahrSumme 1...nWurzel ___________________________spezielle buchung
     * // // // // // Monatssumme 12 _______________________________spezielle buchung
     *
     * LocalDateTime Kodierung
     *
     * // Buchung ________yyyy_mm_dd_h_m>0_ss
     * // Jahresbuchung __yyyy_01_01_0__0__1
     * // Monatsbuchung __yyyy_mm_01_0__0__2
     *
     * // Map<String,Held> ____________________wurzelHeldenMap______Object Attribut
     * // // Map<String,Typ> __________________heldTypenMap_________Object Attribut
     * // // // Map<LocalDateTime,Buchung> ____typBuchungsMap_______Object Attribut
     *
     * Klassenbeziehung - an Properties kann leider kein USERDATA gebunden werden
     *
     * // H0Wurzel
     * // // H1Held
     * // // // H2Typ
     * // // // // Buchung
     */

    private NumberBinding wurzelSumBind;
    private ObservableMap<String, H1Held> wurzelHeldenMap;

    public void initModel() {
        // WURZEL
        wurzelSumBind = new SimpleDoubleProperty(0).add(0);
        wurzelHeldenMap = FXCollections.observableMap(new TreeMap<String, H1Held>());
        for (String h : prj.HELDEN()) {
            // HELDEN
            ObservableMap<String, H2Typ> heldTypenMap = FXCollections.observableMap(new TreeMap<String, H2Typ>());
            H1Held held = new H1Held(heldTypenMap);
            for (String t : prj.TYPEN()) {
                // TYPEN
                ObservableMap<LocalDateTime, Buchung> typenBuchungsMap = FXCollections.observableMap(new TreeMap<LocalDateTime, Buchung>());
                H2Typ typ = new H2Typ(typenBuchungsMap);
                for (int y = prj.JAHRE()[0]; y <= prj.JAHRE()[1]; y++) {
                    // JAHRESSUMMEN
                    SummenBuchung jahrBuchung = SummenBuchung.leereJahressumme(h, t, y);
                    for (int m = 1; m <= 12; m++) {
                        // MONATSSUMMEN
                        SummenBuchung monatBuchung = SummenBuchung.leereMonatssumme(h, t, y, m);
                        //
                        // Monatssummen in Typen stecken
                        typenBuchungsMap.put(monatBuchung.getDate(), monatBuchung);
                    }
                    // Jahressummen in Typen stecken
                    typenBuchungsMap.put(jahrBuchung.getDate(), jahrBuchung);
                }
                // Typen in Helden stecken
                heldTypenMap.put(t, typ);
            }
            // Helden in Wurzel stecken
            wurzelHeldenMap.put(h, held);
        }
    }

    /*
     * Buchung prüfen
     * __________________________________________________________________
     */
    /*
     * Prüfung ob die Buchung zu dieser Wurzel passt (den Arrays entspricht)
     * Wenn nicht, wird ein Fehler geworfen und die Buchung nicht weiter verarbeitet *
     */

    public void checkBuchung(Buchung b) throws IllegalBuchungException {
        String h = b.getHeld().get();
        for (int i = 0; i <= prj.HELDEN().length; i++) {
            if (i == prj.HELDEN().length) {
                throw new IllegalBuchungException(b, "Held nicht in der Wurzel registriert");
            }
            if (h.equals(prj.HELDEN()[i])) { break; }
        }
        String t = b.getTyp().get();
        for (int i = 0; i <= prj.TYPEN().length; i++) {
            if (i == prj.TYPEN().length) {
                throw new IllegalBuchungException(b, "Typ nicht in der Wurzel registriert");
            }
            if (t.equals(prj.TYPEN()[i])) { break; }
        }
        int y = (b.getDate().getYear());
        if (prj.JAHRE()[0] > y || y > prj.JAHRE()[1]) {
            throw new IllegalBuchungException(b, "Jahr nicht im Zeitraum der Wurzel");
        }
    }

    /*
     * Buchung hinzufügen
     * __________________________________________________________________
     */
    /*
     * NUR das Modell darf auch eine Methode nutzen, die die Buchungen ungeprüft einliest
     *
     * Binding der Properties
     */
    public void add(Buchung b) throws IllegalBuchungException {
        checkBuchung(b);
        addChecked(b);
    }

    void addChecked(Buchung b) {

        H1Held h = wurzelHeldenMap.get(b.getHeld().get());
        H2Typ t = h.getHeldTypenMap().get(b.getTyp().get());

        // Buchung einsortieren
        LocalDateTime key = b.getDate();
        while (t.getTypenBuchungsMap().containsKey(key)) {
            b.incrementDateTime();
            key = b.getDate();
        }
        t.getTypenBuchungsMap().put(b.getDate(), b);

        // Wurzelsumme // Heldsumme // Typsumme
        wurzelSumBind = wurzelSumBind.add(b.getBetrag());
        h.setHeldSumme(h.getHeldSumme().add(b.getBetrag()));
        t.setTypSumme(t.getTypSumme().add(b.getBetrag()));

        // Jahressumme anpassen
        LocalDateTime y = SummenBuchung.getJahrSumZeit(b);
        SummenBuchung yS = (SummenBuchung) t.getTypenBuchungsMap().get(y);
        yS.setBuchungSumme(yS.getBuchungSumme().add(b.getBetrag()));
        // Monatssumme anpassen
        LocalDateTime m = SummenBuchung.getMonatSumZeit(b);
        SummenBuchung mS = (SummenBuchung) t.getTypenBuchungsMap().get(m);
        mS.setBuchungSumme(mS.getBuchungSumme().add(b.getBetrag()));

    }

    /*
     * Buchung entfernen
     * __________________________________________________________________
     */
    /*
     * TODO Ist das nötig:
     * Prüfung ob die Buchung zu dieser Wurzel passt (den Arrays entspricht)
     * Wenn nicht, wird ein Fehler geworfen und die Buchung nicht weiter verarbeitet
     *
     * NUR das Modell darf auch eine Methode nutzen, die die Buchungen ungeprüft entfernt
     *
     * Prüfen ob, die Buchung überhaupt enthalten war
     *
     * Binding der Properties
     */

    public void remove(Buchung b) throws IllegalBuchungException {
        checkBuchung(b);
        removeChecked(b);
    }

    public void removeChecked(Buchung b) throws IllegalBuchungException {

        H1Held h = wurzelHeldenMap.get(b.getHeld().get());
        H2Typ t = h.getHeldTypenMap().get(b.getTyp().get());

        // Map auf enthaltene Buchung prüfen
        if (t.getTypenBuchungsMap().containsKey(b.getDate())) {
            if (t.getTypenBuchungsMap().get(b.getDate()).equals(b)) {

                // Buchung rausziehen
                t.getTypenBuchungsMap().remove(b.getDate());

                // Wurzelsumme // Heldsumme // Typsumme
                wurzelSumBind = wurzelSumBind.subtract(b.getBetrag());
                h.setHeldSumme(h.getHeldSumme().subtract(b.getBetrag()));
                t.setTypSumme(t.getTypSumme().subtract(b.getBetrag()));

                // Jahressumme anpassen
                LocalDateTime y = SummenBuchung.getJahrSumZeit(b);
                SummenBuchung yS = (SummenBuchung) t.getTypenBuchungsMap().get(y);
                yS.setBuchungSumme(yS.getBuchungSumme().subtract(b.getBetrag()));
                // Monatssumme anpassen
                LocalDateTime m = SummenBuchung.getMonatSumZeit(b);
                SummenBuchung mS = (SummenBuchung) t.getTypenBuchungsMap().get(m);
                mS.setBuchungSumme(mS.getBuchungSumme().subtract(b.getBetrag()));
            } else {
                throw new IllegalBuchungException(b, "Buchung stimmt nicht mit der TypenBuchungsMap überein");
            }
        } else {
            throw new IllegalBuchungException(b, "Buchung nicht in der TypenBuchungsMap enthalten");
        }
    }

    /*
     * Zugriff durch Presenter - singleSource of truth
     * __________________________________________________________________
     */
    public Projekt getPrj() {
        return prj;
    }

    /*
     * Zugriff durch Presenter
     * __________________________________________________________________
     */

    public ObservableList<Buchung> getTypBuchungsListe(String held, String typ) {
        return FXCollections.observableArrayList(wurzelHeldenMap.get(held).getHeldTypenMap().get(typ).getTypenBuchungsMap().values());
    }

    public NumberBinding getWurzelSumme() {
        return wurzelSumBind;
    }

    public NumberBinding getHeldSumme(String held) {
        return wurzelHeldenMap.get(held).getHeldSumme();
    }

    public NumberBinding getTypSumme(String held, String typ) {
        return wurzelHeldenMap.get(held).getHeldTypenMap().get(typ).getTypSumme();
    }

    public String[] getHeldenXx() {
        // TODO Auto-generated method stub
        return prj.HELDEN();
    }

    public String[] getTypenXx() {
        // TODO Auto-generated method stub
        return prj.TYPEN();
    }

    /*
     * I / O
     * __________________________________________________________________
     */
    public List<Buchung> getBuchungsListe() {
        List<Buchung> liste = new ArrayList<>();
        for (H1Held held : wurzelHeldenMap.values()) {
            for (H2Typ typ : held.getHeldTypenMap().values()) {
                for (Buchung buchung : typ.getTypenBuchungsMap().values()) {
                    if (!buchung.getClass().toString().equals("class model.SummenBuchung")) { liste.add(buchung); }
                }
            }
        }
        return liste;
    }

}
