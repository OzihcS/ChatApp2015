import javax.swing.*;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) throws IOException {
        LogService logService = new LogService();
        logService.start();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Application app = new Application(new GUI());
            }
        });
        logService.end();
    }
}