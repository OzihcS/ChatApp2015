import javax.swing.*;
import javax.swing.event.AncestorListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Contact extends JPanel {
    private String nick;
    private String addr;
    private boolean isOnline;
    private boolean isDelete;

    public Contact(String nick, String addr, boolean isOnline) {
        this.nick = nick;
        this.addr = addr;
        this.isOnline = isOnline;
        JLabel nickLabel = new JLabel(nick);
        nickLabel.setForeground(Colors.gray2);
        nickLabel.setFont(nickLabel.getFont().deriveFont(16f));
        this.add(nickLabel);
        JLabel status = new JLabel();
        status.setMinimumSize(new Dimension(15, 15));
        status.setMaximumSize(new Dimension(15, 15));
        if (isOnline)
            status.setIcon(new ImageIcon(getClass().getResource("gui/frame/userOn.png")));
        else
            status.setIcon(new ImageIcon(getClass().getResource("gui/frame/userOff.png")));
        this.add(status);

        JLabel deleteBtn = new JLabel();
        deleteBtn.setMinimumSize(new Dimension(15, 15));
        deleteBtn.setMaximumSize(new Dimension(15, 15));
        deleteBtn.setIcon(new ImageIcon(getClass().getResource("gui/frame/delete.png")));
        this.add(deleteBtn);
        deleteBtn.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                delete();
            }
        });

        this.setBackground(Colors.dark2);
        this.setMaximumSize(new Dimension(200, 35));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Colors.dark3));
    }

    public String getNick() {
        return this.nick;
    }

    public String getAddr() {
        return this.addr;
    }

    public void delete() {
        this.setVisible(false);
        isDelete = true;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }
}
