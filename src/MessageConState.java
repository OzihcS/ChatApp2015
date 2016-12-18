import javax.swing.*;
import java.awt.*;

public class MessageConState extends JTextArea {

    public MessageConState(String issue) {
        this.setMaximumSize(new Dimension(1000, 60));
        this.setFont(this.getFont().deriveFont(20f));
        this.setForeground(Colors.red);
        this.setBorder(BorderFactory.createLineBorder(Colors.dark1));
        this.setOpaque(false);
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.append(issue);
    }
}
