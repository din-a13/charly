package model.inOut;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import com.google.gson.*;

import model.*;

public class Datei {

    /*
     * statische Attribute, die das Exportieren bestimmen
     * __________________________________________________________________
     */
    private static Charset charset = Charset.forName("UTF-8");
    private static final String dir = "io/";
    private static final String prjSuffix = ".prj.txt";
    private static final String bchSuffix = ".bch.txt";

    /*
     * Dateipfade & Versionskontrolle
     * __________________________________________________________________
     */

    // alle PRIVATE
    // Mutterverzeichniss ist der Projektordner im Workspace
    // Dateipfad zusammen aus standart Direktory, Projektname, Version/ Index und Suffix

    private static Path prjDateiPfad(Projekt prj) {
        String relPfad = dir + prj.name() + "." + int3Strg(prj.versionNr()) + prjSuffix;
        Path absPfad = Paths.get(relPfad).toAbsolutePath();
        System.out.println(absPfad);
        return absPfad;
    }

    private static Path projektIncrDateiPfad(Projekt prj) {
        prj.versionNrIncr();
        return prjDateiPfad(prj);
    }

    private static Path buchDateiPfad(Projekt prj) {
        String relPfad = dir + prj.name() + "." + int3Strg(prj.versionNr()) + "." + int3Strg(prj.buchIdx()) + bchSuffix;
        Path absPfad = Paths.get(relPfad).toAbsolutePath();
        System.out.println(absPfad);
        return absPfad;
    }

    private static Path buchIncrDateiPfad(Projekt prj) {
        prj.buchIdxIncr();
        return buchDateiPfad(prj);
    }

    // Hilfsmethode
    private static String int3Strg(int v) {
        return String.format("%03d", v);
    }

    /*
     * Buchungen IO
     * __________________________________________________________________
     */
    public static void buchExport(H0Wurzel wurzel, Projekt prj) {
        // Dir muss existieren
        try {
            Files.createDirectories(Paths.get(dir));
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        // Anfrage an das Modell eine Liste aller Buchungen auszugeben
        // diese kommt ohne Summenbuchungen an
        String s;
        try (BufferedWriter writer = Files.newBufferedWriter(buchIncrDateiPfad(prj), charset)) {
            for (Buchung b : wurzel.getBuchungsListe()) {
                s = b.toString();
                writer.write(s, 0, s.length());
                writer.newLine();
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public static void buchImport(H0Wurzel wurzel, Projekt prj) throws IllegalBuchungException {
        try (BufferedReader reader = Files.newBufferedReader(buchDateiPfad(prj), charset)) {
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

    public static List<Path> prjPathList() {

        List<Path> prjPathList = new ArrayList<>();
        // Methode aus 400 OI Tutorial Oracle.pdf S.41
        String suche = "*{" + prjSuffix + "}";
        System.out.println(suche);
        Path dirP = Paths.get(dir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirP, suche)) {
            for (Path file : stream) {
                prjPathList.add(file);
                System.out.println(file.getFileName());
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        return prjPathList;
    }

    public static Projekt readProjekt(Path pfad) {
        String line;
        Projekt prj = null;
        // Sting aus Datei lesen
        try (BufferedReader reader = Files.newBufferedReader(pfad, charset)) {
            line = reader.readLine();
            System.out.println(line);
            // mit GSon deserialisieren
            Gson gson = new Gson();
            prj = gson.fromJson(line, Projekt.class);
        } catch (NoSuchFileException nf) {
            System.err.format("IOException: %s%n" + "Leere Datenbank wird erzeugt", nf);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return prj;
    }

    public static void writeProjekt(Projekt prj) {
        // Dir muss existieren
        try {
            Files.createDirectories(Paths.get(dir));
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        // Erzeugt den String mit Gson
        Gson gson = new Gson();
        String line = gson.toJson(prj);

        // Schreibt die Datei
        try (BufferedWriter writer = Files.newBufferedWriter(projektIncrDateiPfad(prj), charset)) {
            writer.write(line, 0, line.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

    }

    /*
     * Standartdatei IO
     * __________________________________________________________________
     */

    // 1.name
    // 2.path - gelesen wird erst mal ein Pfad
    // wenn das nicht fkt. kann auch der Projektname gelesen werden

    private static Path stdPrjDateiPfad() {
        String relPfad = dir + "standart" + ".txt";
        Path absPfad = Paths.get(relPfad).toAbsolutePath();
        System.out.println(absPfad);
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
            System.out.println(name);
            String sp = reader.readLine();
            System.out.println(sp);
            pfad = Paths.get(sp);
        } catch (NoSuchFileException nf) {
            System.err.format("IOException: %s%n" + "Leere Datenbank wird erzeugt", nf);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return pfad;
    }

}
