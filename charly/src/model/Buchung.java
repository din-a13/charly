package model;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

import javafx.beans.property.*;

public class Buchung {
    private int hash;
    private SimpleStringProperty held;
    private SimpleStringProperty typ;
    private LocalDateTime date;
    protected SimpleDoubleProperty betrag;
    private SimpleStringProperty hinweis;

    // _____________________________________________________________
    //
    // Konstruktor
    // _____________________________________________________________

    public Buchung(String h, String t, LocalDateTime d, Number betrag, String hinweis) {
        // Prüfung erfolgt vorher in der Wurzel - die hat heldenListe & typenListe
        this.held = new SimpleStringProperty(h);
        this.typ = new SimpleStringProperty(t);
        this.betrag = new SimpleDoubleProperty();
        this.betrag.setValue(betrag);
        this.hinweis = new SimpleStringProperty(hinweis);

        // Das Datum soll immer auf Sekunden beschnitten werden
        // Minuten >1 beim 1.des Monats
        d = d.truncatedTo(ChronoUnit.SECONDS);
        if (d.getHour() == 0 && d.getMinute() == 0) { d = d.plusMinutes(1); }
        this.date = d;

        // TODO hash erzeugen - dient zur Identifikation von Buchungen zwischen Server mit Mastermodell und Client
        // So können einzelne Buchungen gezielt getauscht werden
        // evt. geht das aber auch einfacher über die bereits bekannten Methoden, die auch das Modell anwendet
        hash = hashFkt(held, typ, date);
    }

    // _____________________________________________________________
    //
    // Setter - nur "Package public"
    // _____________________________________________________________

    void incrementDateTime() {
        date = date.plusSeconds(1);
    }

    void setDateSum(LocalDateTime date) {
        this.date = date;
    }

    // eindeutige Markierung der Buchung
    static int hashFkt(SimpleStringProperty held, SimpleStringProperty typ, LocalDateTime date) {
        return ((date.hashCode() * 13) + held.get().hashCode()) * 13 + typ.get().hashCode();
    }

    // _____________________________________________________________
    //
    // Getter
    // _____________________________________________________________

    public SimpleStringProperty getHeld() {
        return held;
    }

    public SimpleStringProperty getTyp() {
        return typ;
    }

    public SimpleDoubleProperty getBetrag() {
        return betrag;
    }

    public SimpleStringProperty getHinweis() {
        return hinweis;
    }

    public LocalDateTime getDate() {
        return date;
    }

    // _____________________________________________________________
    //
    // I/O
    // _____________________________________________________________

    private static DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'-'HHmmss");

    @Override
    public String toString() {
        String d = date.format(myFormatter);
        return held.get() + ";" + typ.get() + ";" + d + ";" + betrag.get() + ";" + hinweis.get();
    }

    public static Buchung parse(String line) {
        // TODO Lesen eines String
        String[] happen = line.split(";");
        String h = happen[0];
        String t = happen[1];
        LocalDateTime datetime = LocalDateTime.parse(happen[2], myFormatter);
        Number betrag = Double.parseDouble(happen[3]);
        String hinweis = happen[4];
        return new Buchung(h, t, datetime, betrag, hinweis);
    }

}
