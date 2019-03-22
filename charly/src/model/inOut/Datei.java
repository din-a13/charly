package model.inOut;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import javafx.collections.*;

import com.google.gson.*;

import model.*;

public class Datei {

    /*
     * statische Attribute, die das Exportieren bestimmen
     * __________________________________________________________________
     */
    private static Charset charset = Charset.forName("UTF-8");
    private static final String dir = "io/"; // Mutterverzeichniss ist der Projektordner im Workspace
    private static final String prjSuffix = ".prj.txt";
    private static final String bchSuffix = ".bch.txt";

    /*
     * Dateipfade
     * __________________________________________________________________
     */

    // alle PRIVATE
    // Dateipfade zusammengesetzt aus ProjektFolder, Projektname, Version/ Index und Suffix

    private static Path prjDateiPfad(Projekt prj) {
        // Jetzt hat das Projekt einen Pfad hinterlegt
        Path absPfad = prj.getPrjFolderPath().resolve(prj.name() + "." + int3Strg(prj.versionNr()) + prjSuffix);
        return absPfad;
    }

    private static Path buchDateiPfad(Projekt prj) {
        // Jetzt hat das Projekt einen Pfad hinterlegt
        Path absPfad = prj.getPrjFolderPath().resolve(prj.name() + "." + int3Strg(prj.versionNr()) + "." + int3Strg(prj.buchIdx()) + bchSuffix);
        return absPfad;
    }

    // Hilfsmethode nutzt auch die Projektklasse
    static String int3Strg(int v) {
        return String.format("%03d", v);
    }

    /*
     * Buchungen IO
     *
     * Wenn Buchung exportiert wird, muss auch Projekt exportiert werden, damit die Angabe zum BuchIdx konsistent ist
     *
     * Wenn Buchung importiert wird, ist vorher bereits ein Projekt oder eine ganze Projektliste importiert worden
     * __________________________________________________________________
     */

    public static void buchExport(H0Wurzel wurzel) {
        // vor jedem Speichern wird die Version der Buchungsdatei erhöht
        // Falls das Projekt noch auf vrs=0 steht wird diese jetzt auf 1 erhöht - sonnst ist das nicht intuitiv
        wurzel.getPrj().buchIdxIncr();
        if (wurzel.getPrj().versionNr() == 0 && wurzel.getPrj().buchIdx() > 0) { wurzel.getPrj().versionNrIncr(); }

        // Dir muss existieren
        try {
            // Files.createDirectories(Paths.get(dir));
            // Jetzt hat das Projekt einen Pfad hinterlegt
            Files.createDirectories(wurzel.getPrj().getPrjFolderPath());
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        // Anfrage an das Modell eine Liste aller Buchungen auszugeben
        // diese kommt ohne Summenbuchungen an
        String s;
        try (BufferedWriter writer = Files.newBufferedWriter(buchDateiPfad(wurzel.getPrj()), charset)) {
            for (Buchung b : wurzel.getBuchungsListe()) {
                s = b.toString();
                writer.write(s, 0, s.length());
                writer.newLine();
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        // Projekt auch abspeichern
        writeProjekt(wurzel.getPrj(), false);
    }

    // Ansprache durch den Presenter -> Importbutton
    public static void buchExport(H0Wurzel wurzel, Path dirPath) {
        wurzel.getPrj().setPrjFolderPath(dirPath);
        buchExport(wurzel);
    }

    public static void buchImport(H0Wurzel wurzel) throws IllegalBuchungException {
        try (BufferedReader reader = Files.newBufferedReader(buchDateiPfad(wurzel.getPrj()), charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                wurzel.add(Buchung.parse(line));
                System.out.println(line);
            }
        } catch (NoSuchFileException nf) {
            System.err.format("IOException: %s%n" + "Leere Datenbank wird erzeugt", nf);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    /*
     * Projektdatei IO
     * __________________________________________________________________
     */

    public static void changePrjName(H0Wurzel wurzel, String name) {
        wurzel.getPrj().setName(name);
        buchExport(wurzel);
    }

    public static ObservableList<Projekt> prjSammeln() {
        // sucht im Standart io-Ordner (siehe oben final Atribut)
        // erzeugt eine Liste aller vorhandenn Projektpfade
        List<Path> prjPathList = new ArrayList<>();
        // Methode aus 400 OI Tutorial Oracle.pdf S.41
        String suche = "*{" + prjSuffix + "}";
        System.out.print("suche im Standartordnernach: " + suche + " :  ");
        Path dirP = Paths.get(dir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirP, suche)) {
            for (Path file : stream) {
                prjPathList.add(file);
                System.out.println("gefunden: " + file.getFileName());
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }

        // versucht die Projekte an diesen Pfaden zu lesen
        // schreibt das Ergebniss in eine weitere Liste
        ObservableList<Projekt> prjList = FXCollections.observableArrayList();
        for (Path pth : prjPathList) {
            Projekt pr = null;
            pr = Datei.readProjekt(pth);
            if (pr != null) { prjList.add(pr); }
        }
        return prjList;
    }

    // erzeugt / liest ein Projekt aus einem gegebenen Pfad
    // schreibt den Projektordenerpfad - damit alle Daten der Anwnednung im richtigen Ordner landen
    public static Projekt readProjekt(Path pfad) {
        String line;
        Projekt prj = null;
        // Sting aus Datei lesen
        try (BufferedReader reader = Files.newBufferedReader(pfad, charset)) {
            line = reader.readLine();
            System.out.println("gelesen: " + line);
            // mit GSon deserialisieren
            Gson gson = new Gson();
            prj = gson.fromJson(line, Projekt.class);
            prj.setPrjFolderPath(pfad.getParent());
        } catch (NoSuchFileException nf) {
            System.err.format("IOException: %s%n" + "Leere Datenbank wird erzeugt", nf);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        } catch (Exception y) {
            System.err.format("Fehler beim lesen einer Projektdatei: %s%n", y);
        }

        return prj;
    }

    public static void writeProjekt(Projekt prj, boolean increment) {

        // Erzeugt den String mit Gson
        Gson gson = new Gson();
        String line = gson.toJson(prj);

        // Dir muss existieren
        try {
            // Files.createDirectories(Paths.get(dir));
            // Jetzt hat das Projekt einen Pfad hinterlegt
            Files.createDirectories(prj.getPrjFolderPath());
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        // vorher Prüfung ob Incrementschritt gewünscht ist
        // Das mach ich hier, damit sich immer darüber gedanken gemacht wird
        if (increment) { prj.versionNrIncr(); }

        // Schreibt die Datei
        try (BufferedWriter writer = Files.newBufferedWriter(prjDateiPfad(prj), charset)) {
            writer.write(line, 0, line.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    /*
     * Standartdatei IO
     * Aufrufe auch durch PresenterImport
     * Projektname - nur zur Info - wird nicht weiter verwendet
     * Projektpfad - wird zum Laden genutzt
     * letzterNutzer -> steht im Projekt
     * __________________________________________________________________
     */

    // 1.name
    // 2.path - gelesen wird erst mal ein Pfad
    // wenn das nicht fkt. kann auch der Projektname gelesen werden

    private static Path stdPrjDateiPfad() {
        String relPfad = dir + "standart" + ".txt";
        Path absPfad = Paths.get(relPfad).toAbsolutePath();
        return absPfad;
    }

    public static void stdPrjDateiSchreiben(Projekt prj) {
        // Dir muss existieren
        try {
            Files.createDirectories(Paths.get(dir));
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        // Schreibt den Dateipfad und den Projektnamen
        String s;
        try (BufferedWriter writer = Files.newBufferedWriter(stdPrjDateiPfad(), charset)) {
            s = prj.name();
            writer.write(s, 0, s.length());
            writer.newLine();
            s = prjDateiPfad(prj).toString();
            writer.write(s, 0, s.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public static Path stdPrjPath() {
        Path pfad = null;
        try (BufferedReader reader = Files.newBufferedReader(stdPrjDateiPfad(), charset)) {
            String name = reader.readLine();
            String sp = reader.readLine();
            pfad = Paths.get(sp);
            System.out.println("IO/standart.txt: " + name + " : " + sp);
        } catch (NoSuchFileException nf) {
            System.err.format("IOException: %s%n" + " : kein Standart eingetragen", nf);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        } catch (Exception y) {
            System.err.format("Fehler bei Auswertung der standart.txt: %s%n", y);
        }
        return pfad;
    }

    // Standart ProjektOrdner
    // Anwendung nur durch Projekt-Konstruktor, wenn ein neues Projekt erzeugt wird
    static Path stdPrjFolderPath() {
        Path absPfad = Paths.get(dir).toAbsolutePath();
        System.out.println(absPfad);
        return absPfad;
    }

}
