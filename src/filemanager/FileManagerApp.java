package filemanager;

//import java.io.File;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author: Dejan Smiljić; e-mail: dej4n.s@gmail.com
 *
 */
public class FileManagerApp {

    public static void main(String[] args) {

        System.out.print("     \uD83D\uDCC2 FILE MANAGER ← lets you organize files and folders\n");
        System.out.println("\nPick an action from the list below ↓\n");

        for (int i = 0; i < FileManager.values().length - 1; i++) {
            System.out.printf(" • %s\n", FileManager.values()[i]);
        }

        /*
        for (FileManager orders : FileManager.values()) {
            if (!orders.equals(FileManager.UNKNOWN)) {
                System.out.printf(" • %s\n", orders);
            }
        }*/
        System.out.print("\nAction → ");
        Scanner scan = new Scanner(System.in);
        String input = scan.next().toUpperCase();

        FileManager action = FileManager.UNKNOWN;
        try {
            action = FileManager.valueOf(input);
        } catch (Exception mssg) {
            System.err.println(mssg.getMessage());
        }

        switch (action) {

            case LIST:
                System.out.print("Directory to list the contents of → ");
                scan.nextLine();
                input = scan.nextLine();
                File document = new File(input);
                FileManager.folderPreview(document);
                break;
            case INFO:
                System.out.print("File to see info of → ");
                scan.nextLine();
                input = scan.nextLine();
                document = new File(input);
                FileManager.fileInfo(document);
                break;
            case CREATE_DIR:
                System.out.print("Directory to create → ");
                scan.nextLine();
                input = scan.nextLine();
                document = new File(input);
                FileManager.folderCreate(document);
                break;
            case RENAME:
                System.out.print("File to rename → ");
                scan.nextLine();
                input = scan.nextLine();
                document = new File(input);
                System.out.print("File new name → ");
                //scan.nextLine();
                input = scan.nextLine();
                File newDoc = new File(input);
                FileManager.fileRename(document, newDoc);
                break;
            case COPY:
                System.out.print("File to copy → ");
                scan.nextLine();
                input = scan.nextLine();
                document = new File(input);
                System.out.print("Copy where → ");
                //scan.nextLine();
                input = scan.nextLine();
                newDoc = new File(input);
                boolean existsBeforeAction = newDoc.exists();
                FileManager.fileCopy(document, newDoc);
                if (newDoc.exists() && !existsBeforeAction) {
                    System.out.printf("\n%s   copied to   %s\n", document, newDoc.getAbsolutePath());
                }
                break;
            case MOVE:
                System.out.print("File to move → ");
                scan.nextLine();
                input = scan.nextLine();
                document = new File(input);
                System.out.print("Move where → ");
                //scan.nextLine();
                input = scan.nextLine();
                newDoc = new File(input);
                FileManager.fileMove(document, newDoc);
                if (!document.exists() && newDoc.exists()) {
                    System.out.printf("\n%s   moved to   %s\n", document, newDoc.getAbsolutePath());
                }
                break;
            case DELETE:
                System.out.print("File to delete → ");
                scan.nextLine();
                input = scan.nextLine();
                document = new File(input);
                existsBeforeAction = document.exists();
                if (!existsBeforeAction) {
                    System.out.printf("\nCannot find %s\n", document.getAbsolutePath());
                    break;
                }
                if (document.isDirectory()) {
                    long dirSize = FileManager.getFolderSize(document);
                    if (dirSize > 0) {
                        if (dirSize > 1024 * 1024) {
                            System.out.printf("\nWarning - you are about to delete %.2f MB of data found in: \uD83D\uDCC2 %s\n", (double) dirSize / 1024 / 1024, document.getName());
                        } else {
                            System.out.printf("\nWarning - you are about to delete %.2f KB of data found in: \uD83D\uDCC2 %s\n", (double) dirSize / 1024, document.getName());
                        }
                    } else {
                        System.out.printf("\n\uD83D\uDCC2 %s will be deleted.\n", document.getName());
                    }
                } else {
                    System.out.printf("\n\uD83D\uDCC4 %s will be deleted.\n", document.getName());
                }
                System.out.printf("Confirm (y/n): ");
                input = scan.nextLine();
                while (!input.equals("y") && !input.equals("n")) {
                    System.out.print("\ny - delete data / n - cancel\n(y/n): ");
                    input = scan.nextLine();
                }
                if (input.equals("y")) {
                    FileManager.fileDelete(document);
                } else {
                    System.out.println("\nDeletion canceled");
                }
                if (existsBeforeAction && !document.exists()) {
                    System.out.printf("\n%s deleted\n", document);
                }
                break;
            case UNKNOWN:
                System.out.printf("\n\"%s\" - no such action, yet!", input);
                break;

        }

        scan.close();
        System.out.print("\n\uD83D\uDCC2 FILE MANAGER out\n");
    }

}
