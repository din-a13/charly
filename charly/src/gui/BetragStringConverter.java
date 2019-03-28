package gui;

import java.text.*;

import javafx.util.converter.*;

class BetragStringConverter extends NumberStringConverter {

    public BetragStringConverter(NumberFormat betragFormat) {
        super(betragFormat);
    }

    @Override
    public Number fromString(String value) {
        return parseDoubleBetrag(value);
    }

    // bei der bearbeitung muss das + stehen bleiben, sonnst wird automatisch aus + ein -
    @Override
    public String toString(Number value) {
        // If the specified value is null, return a zero-length String
        if (value == null) { return ""; }

        // Create and configure the formatter to be used
        NumberFormat formatter = getNumberFormat();

        // Perform the requested formatting
        String string = formatter.format(value);
        if ((Double) value > 0) { return new String("+" + value); }

        return string;

    }

    // eigene Methoden aus dem Presenter her geholt

    // Eingabeprüfung für Betrag
    public static Double parseDoubleBetrag(String betragString) {
        if (betragString == null) { return Double.valueOf(0.0); }
        // Leerzeichen entfernen
        betragString.trim();
        // wenn leer, dann 0
        if (betragString.equals("")) { return Double.valueOf(0.0); }
        // VORZEICHEN: AUsgaben immer neg. (-)
        char[] betragChrAy = betragString.toCharArray();
        // wenn (-) char=45
        if (betragChrAy[0] == 45) { return -1 * myParser(betragChrAy); }
        // wenn (+) char=43
        if (betragChrAy[0] == 43) { return myParser(betragChrAy); }
        // sonnst immer von neg. Ausgabe ausgehen
        return -1 * myParser(betragChrAy);
    }

    private static Double myParser(char[] betragChrAy) {
        String betragString = "";
        boolean decimal = false;
        // nur Zahlen und das erste Komma/ Punkt werden genommen
        // Rest wird ignoriert
        for (int i = 0; i < betragChrAy.length; i++) {
            if (47 < betragChrAy[i] && betragChrAy[i] < 58) {
                betragString += betragChrAy[i];
            } else {
                if ((betragChrAy[i] == 44 || betragChrAy[i] == 46) & !decimal) {
                    betragString += ".";
                    decimal = true;
                }
            }
        }
        return Double.parseDouble(betragString);
    }

}
