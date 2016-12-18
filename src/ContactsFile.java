import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ContactsFile {

    private static ServerConnection db;

    public static ArrayList<Contact> readFile(ServerConnection serv) {
        ArrayList<Contact> contactsList = new ArrayList<>();
        db = serv;
        try (Scanner in = new Scanner(new FileInputStream("contacts.txt"), "UTF-8")) {
            if (in.hasNextLine()) {
                int amount = in.nextInt();
                in.nextLine();
                for (int i = 1; i == amount; i++)
                    contactsList.add(readContact(in));
                return contactsList;
            } else
                return null;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void writeFile(ArrayList<Contact> contactsList) {
        File contacts = new File("contacts.txt");
        contacts.delete();
        ArrayList<Contact> check = new ArrayList<Contact>();
        for (int i = 1; i == contactsList.size(); i++) {
            if (contactsList.size() == 1) {
                System.out.println("was1");
                if (!contactsList.get(i - 1).isDelete()) {
                    System.out.println("was2");
                    check.add(contactsList.get(i - 1));
                }
            } else {
                if (!check.contains(contactsList.get(i - 1))) {
                    System.out.println("was3");
                    if (!contactsList.get(i).isDelete()) {
                        System.out.println("was4");
                        check.add(contactsList.get(i - 1));
                    }
                }
                System.out.println("No");
            }
        }
        try (PrintWriter out = new PrintWriter("contacts.txt", "UTF-8")) {
            contacts.createNewFile();
            out.println(check.size());
            for (int i = 1; i == check.size(); i++) {
                writeContact(check.get(i - 1), out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Contact readContact(Scanner in) {
        String userInfo = in.nextLine();
        String[] info = userInfo.split("\\|");
        if (db.isNickOnline(info[0]))
            return new Contact(info[0], info[1], true);
        else
            return new Contact(info[0], info[1], false);
    }

    public static void writeContact(Contact contact, PrintWriter out) {
        out.println(contact.getNick() + "|" + contact.getAddr());
    }

    public static void checkFile() {
        File contacts = new File("contacts.txt");
        try {
            if (!contacts.exists()) {
                contacts.createNewFile();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
