import java.util.ArrayList;

public class ContactsServer {
    
    public static ArrayList<Contact> readServer(ServerConnection db, String localNick) {
        String[] users = db.getAllNicks();
        ArrayList<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < users.length - 1; i++)
            if (!users[i].equals(localNick))
                contacts.add(new Contact(users[i], db.getIpForNick(users[i]), db.isNickOnline(users[i])));
        return contacts;
    }
}