package model;

import java.time.*;

import javafx.beans.binding.*;
import javafx.beans.property.*;

public class SummenBuchung extends Buchung {

    /*
     * <monatSumme1>.............<MonatSumme2>
     * kumSum1...................kumSum2
     * ...^.........................^...
     * ...|.........................|...
     * bindVorgKumSum1<----------bindVorgKumSum2
     *
     * kumSum ist Wert - kein Binding!
     * wird mit buchungSumme immer aktualisert
     * sonnst funktioniert Binding mit Helden / Wurzel nicht
     * (die brauchen einen Property - als Variable)
     *
     */
    private NumberBinding bindVorgKumSum;
    private SimpleDoubleProperty kumSum;

    /*
     * buchungSumme
     * <----SimplDoubleProperty buchBetrag1
     * <----SimplDoubleProperty buchBetrag2
     * <----SimplDoubleProperty buchBetrag3
     * ....
     */

    private NumberBinding buchungSumme;

    public SummenBuchung(String h, String t, LocalDateTime dateSum, Number betrag, String hinweis) {
        super(h, t, dateSum, betrag, hinweis);
        this.setDateSum(dateSum);
        buchungSumme = new SimpleDoubleProperty(0).add(0);
        kumSum = new SimpleDoubleProperty(0);
        bindVorgKumSum = new SimpleDoubleProperty(0).add(kumSum);
    }

    // _____________________________________________________________
    //
    // Fabrikmethoden
    // Aufruf nur durch das Modell, daher "Package public"
    // _____________________________________________________________

    static SummenBuchung leereJahressumme(String h, String t, int y) {
        String held = h;
        String typ = t;
        // bzgl. Kodierung vgl. Wurzel
        LocalDateTime date = LocalDateTime.of(y, 1, 1, 0, 0, 1);
        Number betrag = 0;
        String hinweis = "SUMME " + date.getYear();
        return new SummenBuchung(held, typ, date, betrag, hinweis);
    }

    static SummenBuchung leereMonatssumme(String h, String t, int y, int m) {
        String held = h;
        String typ = t;
        // bzgl. Kodierung vgl. Wurzel
        LocalDateTime date = LocalDateTime.of(y, m, 1, 0, 0, 2);
        Number betrag = 0;
        String hinweis = "SUMME " + date.getYear() + " - " + date.getMonthValue();
        return new SummenBuchung(held, typ, date, betrag, hinweis);
    }

    static LocalDateTime getJahrSumZeit(Buchung b) {
        return LocalDateTime.of(b.getDate().getYear(), 1, 1, 0, 0, 1);
    }

    static LocalDateTime getMonatSumZeit(Buchung b) {
        return LocalDateTime.of(b.getDate().getYear(), b.getDate().getMonthValue(), 1, 0, 0, 2);
    }

    // _____________________________________________________________
    //
    // Setter - nur "Package public"
    // _____________________________________________________________

    void setBuchungSumme(NumberBinding buchungSumme) {
        this.buchungSumme = buchungSumme;
        kumSum.set((Double) buchungSumme.getValue());
    }

    void setBindVorgKumSum(NumberBinding bindVorgKumSum) {
        this.bindVorgKumSum = bindVorgKumSum;
    }

    // TODO vielleicht brauch ich dass nicht,
    // wenn ich jedesmal die Summe eintrage (oben)
    void addToKumSum(Double d) {
        kumSum.set(kumSum.get() + d);
    }

    // _____________________________________________________________
    //
    // Getter
    // _____________________________________________________________

    public NumberBinding getBuchungSumme() {
        return buchungSumme;
    }

    public NumberBinding getBindVorgKumSum() {
        return bindVorgKumSum;
    }

    @Override
    /*
     * gibt ein Binding zu allen Observable Values des jeweiligen Zeitraumes zurück
     * so wie Sie im Modell in der ADDBUCHUNG()Methode angelegt werden
     */
    public SimpleDoubleProperty getBetrag() {
        SimpleDoubleProperty toBind = new SimpleDoubleProperty();
        toBind.bind(buchungSumme);
        return toBind;
    }

}
