package model;

public class IllegalBuchungException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -7586841830014459571L;
    private Buchung b;

    public IllegalBuchungException(Buchung b, String s) {
        super(s);
        this.b = b;
    }

}
