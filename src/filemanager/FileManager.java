package filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 *
 * @author Dejan Smiljić; e-mail: dej4n.s@gmail.com
 */
public enum FileManager {

    LIST, INFO, CREATE_DIR, RENAME, COPY, MOVE, DELETE, UNKNOWN;

//<editor-fold defaultstate="collapsed" desc="getFolderSize(); Returns directory size including existing subdirectories">
    public static long getFolderSize(File document) {

        long size = 0;

        File[] file = document.listFiles();
        for (File f : file) {
            if (f.isFile()) {
                size += f.length();
            } else {
                size += getFolderSize(f);// rekurzija
            }
        }

        return size;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="fileInfo(); Displays file properties">
    public static void fileInfo(File document) {

        try {
            if (!document.exists()) {
                System.err.printf("Cannot find %s\n\n", document.getAbsolutePath());
                return;
            }

            if (document.isDirectory()) {
                System.out.printf("\n\uD83D\uDCC2 %s\n-------------------\n", document.getName());
                System.out.println("Type of file:\tDirectory");
            } else {
                System.out.printf("\n\uD83D\uDCC4 %s\n-------------------\n", document.getName());
                System.out.println("Type of file:\tDocument");
            }
            System.out.println("Location:\t" + document.getAbsolutePath());
            if (document.isFile()) {
                System.out.printf("Size:\t\t%.2f KB\n", (double) document.length() / 1024);
            } else {
                long totalSize = getFolderSize(document);// poziv rekurzivne metode
                System.out.printf("Size:\t\t%.2f MB\n", (double) totalSize / 1024 / 1024);
            }

            try {
                FileTime ct = Files.readAttributes(document.toPath(), BasicFileAttributes.class).creationTime();
                LocalDateTime creationTime = ct.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                DateTimeFormatter pattern;
                pattern = DateTimeFormatter.ofPattern("dd. MMMM yyyy. HH:mm:ss", new Locale(/*"SR"*/"EN"));
                System.out.println("Created:\t" + creationTime.format(pattern));
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }

            long modified = document.lastModified();
            Instant instant = Instant.ofEpochMilli(modified);
            //System.out.println("modified: " + instant.atZone(ZoneId.systemDefault()));
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            DateTimeFormatter pattern;
            pattern = DateTimeFormatter.ofPattern("dd. MMMM yyyy. HH:mm:ss", /*Locale.forLanguageTag("sr-LATN-RS")*/ new Locale("EN"));
            System.out.printf("Modified:\t%s\n\n", dateTime.format(pattern));

        } catch (Exception mssg) {
            System.err.println("Input invalid! " + mssg);
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="folderPreview(); Generates a list of files and folders in the specified directory">
    public static void folderPreview(File document) {

        try {
            if (document.exists()) {
                if (document.isDirectory()) {

                    //Niz objekata tipa File. Clanovi niza su svi dokumenti direktorijuma koji
                    File[] files = document.listFiles();//je parametar metode folderPreview()
                    /*Metoda .listFiles() Kreira niz ciji su elementi objekti tipa File.
                Ovi objekti su dokumenti, clanovi direktorijuma nad kojim je metoda pozvana.
                Nad elementima niza odnosno objektima se mogu primjenjivati funkcije za manipulaciju fajlovima.*/

                    System.out.printf("\n\uD83D\uDCC2 %s \n-------------------\n", document.getName());
                    for (File file : files) {
                        if (file.isDirectory()) {
                            System.out.printf("\uD83D\uDCC2 %s\n", file.getPath());
                        } else {
                            System.out.printf("\uD83D\uDCC4 %s\n", file.getPath());
                        }
                    }
                    System.out.printf("found %s files\n\n", files.length);
                } else {
                    System.err.println("Cannot preview the content. File is not a directory.");
                }
            } else {
                System.err.printf("\nCannot find %s\n\n", document);
            }
        } catch (Exception mssg) {
            System.err.println("Input invalid! " + mssg);
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="folderCreate(); Creates directory">
    public static void folderCreate(File document) {
        try {
            if (!document.exists()) {
                if (!document.mkdir()) {
                    System.err.printf("Error! Cannot create %s\n", document);
                } else {
                    System.out.printf("\nDirectory created:\t%s\n", document.getAbsolutePath());
                }
            } else {
                System.err.printf("\nCannot create %s\n", document.getAbsolutePath());
                System.out.printf("%s already exists\n", document.getAbsolutePath());
            }
        } catch (Exception mssg) {
            System.out.println(mssg.getMessage());
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="fileRename(); Renames file">
    public static void fileRename(File document, File newName) {

        try {
            /* - ukoliko navedene putanje nemaju isti roditeljski direktorijum,
        metoda renameTo(), sem sto preimenuje dokument
        takodje PREMJESTA!! dokument. */
 /*
        
        //jednostavnije rjesenje
        //dokument ce biti preimenovan samo ako je navedena apsolutna putanja i
        //ako su roditeljski direktorijumi jednaki
        
        
        if (!document.exists()) {
        System.err.printf("%s not found\n\n", document);
        return;
        }
        if (newName.exists()) {
        System.err.printf("Cannot rename %s\n", document);
        System.out.println(newName.getAbsolutePath() + " alredy exists\n");
        return;
        }
        
        if (newName.isAbsolute() && (document.getParent().equals(newName.getParent()))) {
        if (document.renameTo(newName)) {
        System.out.println(document.getAbsolutePath() + " renamed to " + newName.getAbsolutePath());
        } else {
        System.err.printf("Cannot rename %s\n\n", document);
        }
        } else {
        System.err.print("Location invalid!\n");
        System.out.printf("Cannot rename %s\n\n", document.getAbsolutePath());
        }
             */
            //ako je kod iznad "otkomentarisan", "zakomentarisi" sve linije koda ispod
            if (!document.exists()) {
                System.err.printf("Can not find %s\n\n", document.getAbsolutePath());
                return;
            }

            if (!document.isDirectory()) {
                String[] extension = (document.getName()).split("\\.");
                if (newName.isAbsolute()) {
                    if (!newName.getParentFile().exists()) {
                        System.out.printf("\nCannot find %s\n", newName.getParent());
                        return;
                    }
                    if (document.getParent().equals(newName.getParent())) {
                        if (newName.getName().endsWith("." + extension[extension.length - 1])) {
                            if (newName.exists()) {
                                System.err.printf("Cannot rename %s\n", document);
                                System.out.println(newName.getAbsolutePath() + " alredy exists\n");
                                return;
                            }
                            if (document.renameTo(newName)) {
                                System.out.println(document.getAbsolutePath() + " renamed to " + newName.getAbsolutePath());
                            } else {
                                System.err.printf("Cannot rename %s\n\n", document);
                                return;
                            }
                        } else {
                            File newDocNameExtended = new File(newName.getAbsolutePath() + "." + extension[extension.length - 1]);
                            if (newDocNameExtended.exists()) {
                                System.err.printf("Cannot rename %s\n", document);
                                System.out.println(newName.getAbsolutePath() + " alredy exists\n");
                                return;
                            }
                            if (document.renameTo(newDocNameExtended)) {
                                System.out.println(document.getAbsolutePath() + " renamed to " + newDocNameExtended.getAbsolutePath());
                            } else {
                                System.err.printf("Cannot rename %s\n\n", document);
                            }
                        }
                        return;
                    }
                }
                //ako je unijeta relativna putanja 
                if (newName.getName().endsWith("." + extension[extension.length - 1])) {
                    File newDocName = new File(document.getParent() + File.separator + newName.getName());
                    if (newDocName.exists()) {
                        System.err.printf("Cannot rename %s\n", document);
                        System.out.println(newDocName.getAbsolutePath() + " alredy exists\n");
                        return;
                    }
                    if (document.renameTo(newDocName)) {
                        System.out.println("Warning: Path mismatch. File relocation prevented. ");
                        System.out.println(document.getAbsolutePath() + " renamed to " + newDocName.getAbsolutePath());
                    } else {
                        System.err.printf("Cannot rename %s\n\n", document.getAbsolutePath());

                    }
                } else {
                    File docNewNameExtended = new File(document.getParent() + File.separator + newName.getName() + "." + extension[extension.length - 1]);
                    if (docNewNameExtended.exists()) {
                        System.err.printf("Cannot rename %s\n", document);
                        System.out.println(docNewNameExtended.getAbsolutePath() + " alredy exists\n");
                        return;
                    }
                    if (document.renameTo(docNewNameExtended)) {
                        System.out.println("Warning: Path mismatch. File relocation prevented. ");
                        System.out.println(document.getAbsolutePath() + " renamed to " + docNewNameExtended.getAbsolutePath());
                    } else {
                        System.err.printf("Cannot rename %s\n\n", document.getAbsolutePath());

                    }
                }

            } else {//blok koda koji se izvrsava ako je dokument direktorijum
                //ako je za novo ime koristena apsolutna putanja
                if (newName.isAbsolute()) {

                    if (!newName.getParentFile().exists()) {
                        System.out.printf("\nCannot find %s\n", newName.getParent());
                        return;
                    }
                    //ukoliko su roditeljski direktorijumi jednaki pokusace se izvrsiti preimenovanje dokumenta
                    if (document.getParent().equals(newName.getParent())) {
                        if (newName.exists()) {
                            System.err.printf("Cannot rename %s\n", document);
                            System.out.println(newName.getAbsolutePath() + " alredy exists\n");
                            return;
                        }
                        if (document.renameTo(newName)) {
                            System.out.println(document.getAbsolutePath() + " renamed to " + newName.getAbsolutePath());
                        } else {
                            System.err.printf("Cannot rename %s\n\n", document);
                        }
                        return;
                    }
                    //ako roditeljski direktorijumi nisu jednaki, ili ako je...
                }
                //... putanja relativna sprecava se premjestanje dokumenta i pokusava se preimenovati dokument.
                File newDirName = new File(document.getParent() + File.separator + newName.getName());
                if (newDirName.exists()) {
                    System.err.printf("Cannot rename %s\n", document);
                    System.out.println(newDirName.getAbsolutePath() + " alredy exists\n");
                } else {
                    if (document.renameTo(newDirName)) {
                        System.out.println("Warning: Path mismatch. File relocation prevented. ");
                        System.out.println(document.getAbsolutePath() + " renamed to " + newDirName.getAbsolutePath());
                    } else {
                        System.err.printf("Cannot rename %s\n\n", document.getAbsolutePath());
                    }
                }

            }
        } catch (Exception mssg) {
            System.err.println(mssg + ";" + mssg.getMessage());
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="fileCopy(); Copies file">
    public static void fileCopy(File document, File documentReplica) {

        try {

            if (!document.exists()) {
                System.err.printf("Cannot find %s\n", document.getPath());
                return;
            }

            if (!documentReplica.isAbsolute()) {

                System.out.print("\nWarning - relative path input\n");
                documentReplica = new File(documentReplica.getAbsolutePath());
                /*
            //kod ispod onemogucava koristenje relativne putanje, ispisuje poruku i izlazi iz fileCopy() metode
            
            System.err.print("Invalid path; Location missing!\n");
            System.out.printf("Cannot copy %s to %s", document, documentReplica);
            return;
                 */
            }

            if (!documentReplica.getParentFile().exists()) {
                System.out.printf("\nCannot find %s\n", documentReplica.getParent());
                return;
            }

            if (documentReplica.getParent().toLowerCase().contains(document.getName().toLowerCase())) {

                //onemogucava beskonacno kopiranje sadrzaja (!?)
                System.err.printf("\nCannot copy %s to %s\n", document, documentReplica);
                System.out.println("The destination folder is a subfolder of the source folder\n");
                return;
            }

            if (document.isDirectory()) {
                if (!documentReplica.exists()) {
                    if (!documentReplica.mkdir()) {

                        System.err.printf("Cannot create %s", documentReplica);
                        return;
                    }
                } else {
                    System.err.printf("\n%s already exists...\n", documentReplica.getAbsolutePath());
                    return;
                }

                File[] files = document.listFiles();/*Niz objekata tipa File. Elementi
            niza su svi dokumenti, clanovi direktorijuma koji je naveden kroz fajl putanju*/
                for (File file : files) {
                    File originFile = new File(document, file.getName());
                    File replicaFile = new File(documentReplica, file.getName());
                    fileCopy(originFile, replicaFile);//rekurzija

                }

            } else {

                if (!documentReplica.exists()) {
                    try (FileInputStream fis = new FileInputStream(document);
                            FileOutputStream fos = new FileOutputStream(documentReplica)) {

                        byte[] buffer = new byte[1024];
                        int length;

                        while ((length = fis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }

                    } catch (IOException mssg) {

                        System.err.println(mssg.getMessage());
                    }
                } else {
                    System.err.printf("\n%s already exists \n", documentReplica.getAbsolutePath());

                }
            }
        } catch (Exception mssg) {
            System.err.println(mssg + ";" + mssg.getMessage());
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="fileMove(); Moves file (Cut/Paste)">
    public static void fileMove(File document, File documentNewLocation) {

        try {

            if (!document.exists()) {
                System.err.printf("Cannot find %s\n", document.getPath());
                return;
            }

            if (!documentNewLocation.isAbsolute()) {

                /*
                //omogucava koristenje relativne putanje uz upozorenje
                System.out.print("\nWarning - relative path input");
                documentNewLocation = new File(documentNewLocation.getAbsolutePath());*/
                System.err.print("\nInvalid path; Location missing!\n");
                System.out.printf("Cannot move %s to %s\n\n", document, documentNewLocation);
                return;

            }

            if (!documentNewLocation.getParentFile().exists()) {
                System.out.printf("\nCannot find %s\n", documentNewLocation.getParent());
                return;
            }

            if (documentNewLocation.getParent().toLowerCase().contains(document.getName().toLowerCase())) {

                //onemogucava beskonacno kopiranje sadrzaja (!?)
                System.err.printf("\nCannot move %s to %s\n", document, documentNewLocation);
                System.out.println("The destination folder is a subfolder of the source folder\n");
                return;
            }

            if (document.isDirectory()) {
                if (!documentNewLocation.exists()) {
                    documentNewLocation.mkdir();
                } else {
                    System.err.printf("%s already exists\n", documentNewLocation);
                    return;
                }

                File[] files = document.listFiles();/*Niz objekata tipa File. Elementi
            niza su svi dokumenti, clanovi direktorijuma koji je naveden kroz fajl putanju*/

                for (File file : files) {
                    File originFile = new File(document, file.getName());
                    File replicaFile = new File(documentNewLocation, file.getName());
                    fileMove(originFile, replicaFile);// - rekurzija

                }

                document.delete();

            } else {

                if (!documentNewLocation.exists()) {
                    try (FileInputStream fis = new FileInputStream(document);
                            FileOutputStream fos = new FileOutputStream(documentNewLocation)) {

                        byte[] buffer = new byte[1024];
                        int length;

                        while ((length = fis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);

                        }

                    } catch (IOException mssg) {

                        System.err.println(mssg.getMessage());
                    }

                    document.delete();

                } else {
                    System.err.printf("%s already exists \n\n", documentNewLocation);
                }
            }
        } catch (Exception mssg) {
            System.err.println(mssg + ";" + mssg.getMessage());
        }
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="fileDelete(); Deletes file">
    public static void fileDelete(File document) {

        try {

            if (!document.exists()) {
                System.err.printf("Cannot find %s\n", document.getAbsolutePath());
                return;
            }

            if (document.isDirectory()) {

                File[] files = document.listFiles();

                for (File file : files) {
                    File memberFile = new File(document, file.getName());
                    fileDelete(memberFile);// rekurzija.
                }

                if (!document.delete()) {
                    System.err.printf("Cannot delete %s", document.getAbsolutePath());
                }

            } else {
                if (!document.delete()) {
                    System.err.printf("Cannot delete %s", document.getAbsolutePath());
                }

            }
        } catch (Exception mssg) {
            System.err.println(mssg + ";" + mssg.getMessage());
        }
    }
//</editor-fold>

}
