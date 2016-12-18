import javax.swing.*;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;

/*

GRAPHICAL USER INTERFACE


 */

public class GUI extends JFrame {
    private JTextField textLocalNick = new JTextField();
    private JTextField textRemoteAddress = new JTextField();
    private JTextField textRemoteNick = new JTextField();
    private JTextField textWriteMsg = new JTextField();

    private JPanel leftBar = new JPanel();
    private JPanel textHistoryMsg = new JPanel();
    private JPanel locConts = new JPanel();
    private JPanel allConts = new JPanel();

    private JLabel remoteAddress = new JLabel("Addr");
    private JLabel remoteNick = new JLabel("Nick");

    private JLabel btnSendFile = new JLabel("");
    private JLabel btnApply = new JLabel("");
    private JLabel btnConnect = new JLabel("");
    private JLabel btnDisconnect = new JLabel("");
    private JLabel btnUserAdd = new JLabel("");
    private JLabel userLogo;

    private JScrollPane msgHistScroll = new JScrollPane(textHistoryMsg);
    private JScrollPane locContsScroll = new JScrollPane(locConts);
    private JScrollPane allContsScroll = new JScrollPane(allConts);

    private JTabbedPane contsTabs = new JTabbedPane();

    private Incoming incoming;

    private Font segoeRegFont;    // ..Reg = Regular
    private Font shrutiRegFont;
    private Font hpLightFont;


    public GUI() {
        // Frame
        int toWidth = (int) ((Toolkit.getDefaultToolkit().getScreenSize().getWidth()
                - Constants.FRAME_WIDTH_DEFAULT) / 2);
        int toHeight = (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight()
                - Constants.FRAME_HEIGHT_DEFAULT) / 2);
        this.setBounds(toWidth, toHeight, Constants.FRAME_WIDTH_DEFAULT, Constants.FRAME_HEIGHT_DEFAULT);
        this.setMinimumSize(new Dimension(Constants.FRAME_WIDTH_DEFAULT, Constants.FRAME_HEIGHT_DEFAULT));
        this.setTitle(Constants.DEFAULT_VER);
        this.setLayout(null);
        getContentPane().setBackground(Colors.dark1);


        // Fonts importing
        try {
            segoeRegFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("gui/fonts/segoeui.ttf")).deriveFont(20f);
            shrutiRegFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("gui/fonts/tai.ttf")).deriveFont(30f);
            hpLightFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("gui/fonts/hp.ttf")).deriveFont(18f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment().getLocalGraphicsEnvironment();
            ge.registerFont(segoeRegFont);
            ge.registerFont(shrutiRegFont);
            ge.registerFont(hpLightFont);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        // ToolTip handling (changing it's style)
        UIManager.put("ToolTip.background", Color.WHITE);
        UIManager.put("ToolTip.foreground", Color.LIGHT_GRAY);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(Color.WHITE));

        // Custom ScrollBar
        msgHistScroll.getVerticalScrollBar().setUI(new CustomScrollBar());
        allContsScroll.getVerticalScrollBar().setUI(new CustomScrollBar());
        locContsScroll.getVerticalScrollBar().setUI(new CustomScrollBar());

        // Left bar
        leftBar.setBackground(Colors.dark3);
        leftBar.setLayout(null);
        leftBar.setBorder(BorderFactory.createEtchedBorder(1));
        this.add(leftBar);

        // Local nick
        textLocalNick.setFont(hpLightFont.deriveFont(18f));
        textLocalNick.setForeground(Colors.brwn1);
        textLocalNick.setDisabledTextColor(Colors.gray1);
        textLocalNick.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        textLocalNick.setBounds(25, 30, 150, 30);
        leftBar.add(textLocalNick);

        btnApply.setIcon(new ImageIcon(getClass().getResource("gui/frame/applyIcon.png")));
        btnApply.setDisabledIcon(new ImageIcon(getClass().getResource("gui/frame/applyDsbl.png")));
        btnApply.setBounds(175, 30, 60, 30);
        leftBar.add(btnApply);

        JLabel bg = new JLabel("");
        bg.setIcon(new ImageIcon(DummyImage.create(262, 85, Colors.brwn1)));
        bg.setBounds(2, 0, 260, 85);
        leftBar.add(bg);


        // Adding cont
        btnUserAdd.setIcon(new ImageIcon(getClass().getResource("gui/frame/userAddIcon.png")));
        btnUserAdd.setBounds(150, 175, 25, 25);
        leftBar.add(btnUserAdd);

        userLogo = new JLabel("");
        userLogo.setIcon(new ImageIcon(getClass().getResource("gui/frame/user.png")));
        userLogo.setBounds(85, 108, 90, 90);
        leftBar.add(userLogo);


        // Remote info
        textRemoteNick.setBounds(100, 225, 135, 25);
        textRemoteNick.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        textRemoteNick.setFont(hpLightFont.deriveFont(15f));
        textRemoteNick.setDisabledTextColor(Colors.gray1);
        textRemoteNick.setEnabled(false);
        leftBar.add(textRemoteNick);

        textRemoteAddress.setBounds(100, 265, 135, 25);
        textRemoteAddress.setFont(hpLightFont.deriveFont(15f));
        textRemoteAddress.setForeground(Colors.brwn1);
        textRemoteAddress.setDisabledTextColor(Colors.gray1);
        textRemoteAddress.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        // textRemoteAddress.setEnabled(false);
        leftBar.add(textRemoteAddress);

        btnConnect.setBounds(25, 310, 105, 30);
        btnConnect.setIcon(new ImageIcon(getClass().getResource("gui/frame/conIcon.png")));
        btnConnect.setDisabledIcon(new ImageIcon(getClass().getResource("gui/frame/conDsbl.png")));
        btnConnect.setEnabled(false);
        leftBar.add(btnConnect);

        btnDisconnect.setBounds(130, 310, 105, 30);
        btnDisconnect.setIcon(new ImageIcon(getClass().getResource("gui/frame/disconIcon.png")));
        btnDisconnect.setDisabledIcon(new ImageIcon(getClass().getResource("gui/frame/disconDsbl.png")));
        btnDisconnect.setEnabled(false);
        leftBar.add(btnDisconnect);

        remoteNick.setBounds(25, 225, 60, 25);
        remoteNick.setFont(hpLightFont.deriveFont(14f));
        remoteNick.setForeground(Colors.brwn1);
        leftBar.add(remoteNick);

        remoteAddress.setBounds(25, 265, 60, 25);
        remoteAddress.setFont(hpLightFont.deriveFont(14f));
        remoteAddress.setForeground(Colors.brwn1);
        leftBar.add(remoteAddress);


        // Contacts
        locConts.setBackground(Colors.dark3);
        locConts.setLayout(new BoxLayout(locConts, BoxLayout.Y_AXIS));
        locContsScroll.setBackground(Colors.dark3);
        locContsScroll.setBorder(BorderFactory.createLineBorder(Colors.dark3));
        locConts.setEnabled(false);

        allConts.setBackground(Colors.dark3);
        allConts.setLayout(new BoxLayout(allConts, BoxLayout.Y_AXIS));
        allContsScroll.setBackground(Colors.dark3);
        allContsScroll.setBorder(BorderFactory.createLineBorder(Colors.dark3));
        allConts.setEnabled(false);

        contsTabs.add("Local", locContsScroll);
        contsTabs.add("All", allContsScroll);
        contsTabs.setBackground(Colors.dark2);
        contsTabs.setForeground(Colors.gray1);
        contsTabs.setTabPlacement(3);
        UIManager.put("TabbedPane.selected", Colors.dark1);
        UIManager.put("TabbedPane.borderColor", Colors.dark1);
        UIManager.put("TabbedPane.focus", Colors.dark1);
        contsTabs.setUI(new CustomTabbedPane());
        contsTabs.setEnabled(false);
        leftBar.add(contsTabs);


        // Messaging
        msgHistScroll.setBorder(null);
        textHistoryMsg.setBackground(Colors.dark1);
        textHistoryMsg.setLayout(new BoxLayout(textHistoryMsg, BoxLayout.Y_AXIS));
        textWriteMsg.setFont(segoeRegFont);
        textWriteMsg.setForeground(Colors.gray2);
        textWriteMsg.setBackground(Colors.dark3);
        textWriteMsg.setBorder(BorderFactory.createLineBorder(Colors.dark3, 2));
        btnSendFile.setIcon(new ImageIcon(getClass().getResource("gui/frame/sendIcon.png")));
        btnSendFile.setDisabledIcon(new ImageIcon(getClass().getResource("gui/frame/sendDsbl.png")));
        this.add(msgHistScroll);
        this.add(textWriteMsg);
        this.add(btnSendFile);
        textWriteMsg.setEnabled(false);
        btnSendFile.setEnabled(false);
        textHistoryMsg.setEnabled(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // Getters & Setters
    public String getLocalNick() {
        return this.textLocalNick.getText();
    }

    public String getRemoteAddress() {
        return this.textRemoteAddress.getText();
    }

    public void setLocalNick(String nick) {
        this.textLocalNick.setText(nick);
    }

    public String getRemoteNick() {
        return this.textRemoteNick.getText();
    }

    public void setRemoteNick(String remoteNick) {
        this.textRemoteNick.setText(remoteNick);
    }

    public void setRemoteAddress(String remoteAddress) {
        this.textRemoteAddress.setText(remoteAddress);
    }

    public String getMsg() {
        String msg = this.textWriteMsg.getText();
        this.textWriteMsg.setText("");
        return msg;
    }

    public String getMsgText() {
        return this.textWriteMsg.getText();
    }

    public void setMsg(String msg) {
        this.textWriteMsg.setText(msg);
    }

    // Check enabled
    public boolean sendFileIsEnabled() {
        return this.btnSendFile.isEnabled();
    }

    public boolean applyIsEnabled() {
        return this.btnApply.isEnabled();
    }

    public boolean conIsEnabled() {
        return this.btnConnect.isEnabled();
    }

    public boolean disconIsEnabled() {
        return this.btnDisconnect.isEnabled();
    }

    public boolean userAddIsEnabled() {
        return this.btnUserAdd.isEnabled();
    }

    public boolean incomingVisible() {
        return this.incoming.isVisible();
    }

    // Set enabled
    public void setConnected() {
        this.btnConnect.setEnabled(false);
        this.btnDisconnect.setEnabled(true);
        this.textWriteMsg.setEnabled(true);
        this.textHistoryMsg.removeAll();
        this.btnSendFile.setEnabled(true);
    }

    public void setDisconnected() {
        this.btnDisconnect.setEnabled(false);
        this.textWriteMsg.setEnabled(false);
        this.textHistoryMsg.setEnabled(false);
        this.btnSendFile.setEnabled(false);

        this.textRemoteAddress.setEnabled(true);
        this.textRemoteAddress.setText("");
        this.textRemoteNick.setText("");
        this.btnConnect.setEnabled(true);
        this.contsTabs.setEnabled(true);
    }

    public void setApplied() {
        this.btnApply.setEnabled(false);
        this.textLocalNick.setEnabled(false);
        this.btnConnect.setEnabled(true);
        this.contsTabs.setEnabled(true);
        this.locConts.setEnabled(true);
        this.allConts.setEnabled(true);
    }

    public void dialogSetVisible(boolean visible) {
        this.incoming.setVisible(visible);
    }

    // Adding new text to the text area
    public void appendMsg(String msg) {
        this.textHistoryMsg.add(new Message(this.textRemoteNick.getText(), LocalDateTime.now().toString(),
                msg, false));
        this.revalidate();

    }

    public void appendMyMsg(String msg) {
        this.textHistoryMsg.add(new Message(this.textLocalNick.getText(), LocalDateTime.now().toString(),
                msg, true));
        this.revalidate();
    }

    public void appendBroken(String issue) {
        this.textHistoryMsg.add(new MessageConState(issue));
        this.revalidate();
    }

    public void cleanHist() {
        this.textHistoryMsg.removeAll();
    }

    // Contacs adding
    public void addServerContact(Contact cont) {
        this.allConts.add(cont);
        this.allConts.revalidate();
    }

    public void addLocalContact(Contact cont) {
        this.locConts.add(cont);
        this.locConts.revalidate();
    }

    // Listeners
    public void addSendFileListener(MouseListener sendFileListener) {
        this.btnSendFile.addMouseListener(sendFileListener);
    }


    public void addApplyListener(MouseListener applyListener) {
        this.btnApply.addMouseListener(applyListener);
    }

    public void addConnectListener(MouseListener connectListener) {
        this.btnConnect.addMouseListener(connectListener);
    }

    public void addDisconnectListener(MouseListener disconListener) {
        this.btnDisconnect.addMouseListener(disconListener);
    }

    public void addMsgKeyListener(KeyListener keyListener) {
        this.textWriteMsg.addKeyListener(keyListener);
    }

    public void addNickKeyListener(KeyListener keyListener) {
        this.textLocalNick.addKeyListener(keyListener);
    }

    public void addAddrKeyListener(KeyListener keyListener) {
        this.textRemoteAddress.addKeyListener(keyListener);
    }

    public void addAcceptListener(MouseListener mouseListener) {
        this.incoming.acceptAddListener(mouseListener);
    }

    public void addRejectListener(MouseListener mouseListener) {
        this.incoming.rejectBtn.addMouseListener(mouseListener);
    }

    public void addUserAddListener(MouseListener mouseListener) {
        this.btnUserAdd.addMouseListener(mouseListener);
    }

    // Changing icons from Application class
    public void setSendFileIcon(ImageIcon icon) {
        this.btnSendFile.setIcon(icon);
    }


    public void setApplyIcon(ImageIcon icon) {
        this.btnApply.setIcon(icon);
    }

    public void setConnectIcon(ImageIcon icon) {
        this.btnConnect.setIcon(icon);
    }

    public void setDisconnectIcon(ImageIcon icon) {
        this.btnDisconnect.setIcon(icon);
    }

    public void setAcceptIcon(ImageIcon icon) {
        this.incoming.setAcIcon(icon);
    }

    public void setRejectIcon(ImageIcon icon) {
        this.incoming.setRejIcon(icon);
    }

    public void setUserAddIcon(ImageIcon icon) {
        this.btnUserAdd.setIcon(icon);
    }

    // Changing location
    public void setLocations() {
        this.leftBar.setBounds(-2, -2, 264, getHeight() - 35);
        this.msgHistScroll.setBounds(311, 20, getWidth() - 376, getHeight() - 120);
        this.textWriteMsg.setBounds(311, getHeight() - 87, msgHistScroll.getWidth() - 60, 30);
        this.btnSendFile.setBounds(textWriteMsg.getX() + textWriteMsg.getWidth(), textWriteMsg.getY(), 60, 30);
        /////////////////////////////////////////////
        this.contsTabs.setBounds(30, 350, 200, leftBar.getY() + leftBar.getHeight() - 358);
        this.validate();
    }

    public JTextField getTextWriteMsg(){
        return this.textWriteMsg;
    }

    // Showing incoming request
    public void incomingCall(String userNick) {
        incoming = new Incoming(userNick);
    }

    // Dialog of incoming call
    private class Incoming extends JDialog {
        private JLabel acceptBtn = new JLabel("");
        private JLabel rejectBtn = new JLabel("");
        private JLabel user = new JLabel("");

        public Incoming(String userNick) {
            this.setUndecorated(true);
            this.setLayout(null);
            GraphicsEnvironment env =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            this.setBounds(0, (int) (env.getMaximumWindowBounds().getHeight() - 50), 200, 50);
            this.setBackground(Colors.brwn1);
            getContentPane().setBackground(Colors.brwn1);
            acceptBtn.setBounds(120, 9, 32, 32);
            acceptBtn.setIcon(new ImageIcon(getClass().getResource("gui/dialog/acIcon.png")));
            this.add(acceptBtn);
            rejectBtn.setBounds(158, 9, 32, 32);
            rejectBtn.setIcon(new ImageIcon(getClass().getResource("gui/dialog/rejIcon.png")));
            this.add(rejectBtn);
            user.setText(userNick);
            user.setBounds(10, 15, 100, 20);
            user.setFont(hpLightFont.deriveFont(18f));
            user.setForeground(Colors.gray1);
            this.add(user);
            this.setEnabled(true);
            this.setAlwaysOnTop(true);
            this.setVisible(true);
        }

        public void acceptAddListener(MouseListener mouseListener) {
            this.acceptBtn.addMouseListener(mouseListener);
        }

        public void rejectAddListener(MouseListener mouseListener) {
            this.rejectBtn.addMouseListener(mouseListener);
        }

        public void setAcIcon(ImageIcon icon) {
            this.acceptBtn.setIcon(icon);
        }

        public void setRejIcon(ImageIcon icon) {
            this.rejectBtn.setIcon(icon);
        }
    }

    // Custom ScrollBar
    private class CustomScrollBar extends MetalScrollBarUI {
        private Image imageThumb, imageTrack;
        private JButton b = new JButton() {

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }

        };

        public CustomScrollBar() {
            imageThumb = DummyImage.create(32, 32, new Color(38, 38, 38));
            imageTrack = DummyImage.create(32, 32, new Color(45, 45, 45));
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            ((Graphics2D) g).drawImage(imageThumb,
                    r.x, r.y, r.width, r.height, null);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            ((Graphics2D) g).drawImage(imageTrack,
                    r.x, r.y, r.width, r.height, null);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return b;
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return b;
        }
    }

    private static class DummyImage {

        static public Image create(int w, int h, Color c) {
            BufferedImage bugImg = new BufferedImage(
                    w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bugImg.createGraphics();
            g2d.setPaint(c);
            g2d.fillRect(0, 0, w, h);
            g2d.dispose();
            return bugImg;
        }
    }

    // Custom TabbedPane
    private class CustomTabbedPane extends MetalTabbedPaneUI {
        @Override
        protected void paintBottomTabBorder(int tabIndex, Graphics g, int x, int y, int w, int h, int btm, int rght, boolean isSelected) {
        }

        @Override
        protected void paintLeftTabBorder(int tabIndex, Graphics g, int x, int y, int w, int h, int btm, int rght, boolean isSelected) {
        }

        @Override
        protected void paintRightTabBorder(int tabIndex, Graphics g, int x, int y, int w, int h, int btm, int rght, boolean isSelected) {
        }

        @Override
        protected void paintTopTabBorder(int tabIndex, Graphics g, int x, int y, int w, int h, int btm, int rght, boolean isSelected) {
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        }
    }
}
