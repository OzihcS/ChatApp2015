import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/*

CLASS THAT CONTROLS THE SET

 */

public class Application {
    private ArrayList<Contact> allConts;
    private ArrayList<Contact> locConts;
    private ServerConnection server;
    private Connection connection;
    private Caller caller;
    private CallListenerThread callListenerThread;
    private CommandListenerThread commandListenerThread;
    private GUI frame;

    public Application(GUI frame) {
        this.frame = frame;
        this.frame.addWindowStateListener(new StateListener());
        this.frame.addWindowListener(new FrameListener());
        this.frame.addSendFileListener(new SendFileListener());
        this.frame.addApplyListener(new BtnApplyListener());
        this.frame.addConnectListener(new BtnConListener());
        this.frame.addDisconnectListener(new BtnDisconListener());
        this.frame.addNickKeyListener(new NickTextListener());
        this.frame.addAddrKeyListener(new RemoteAddressListener());
        this.frame.addMsgKeyListener(new WriteMsgListener());
        this.frame.addComponentListener(new ResizeListener());
        this.frame.addUserAddListener(new BtnUserAddListener());
    }


    public void acceptIncomingCall() {
        this.frame.setRemoteAddress(connection.getSocketAddress().toString());
        this.frame.setRemoteNick(((NickCommand) commandListenerThread.getLastCommand()).getNick());
        this.connection.send(Command.CommandType.ACCEPT.toString());
        this.frame.cleanHist();
        this.frame.setConnected();
        this.callListenerThread.setBusy(true);
        commandListenerThread = new CommandListenerThread(connection);
        addCommandObserver();
        commandListenerThread.start();
    }

    public void rejectIncomingCall() throws IOException {
        this.connection = callListenerThread.getLastCon();
        this.connection.send(Command.CommandType.REJECT.toString());
        this.commandListenerThread.stop();
        this.callListenerThread.setBusy(false);
        this.connection.close();
    }

    public void addCommandObserver() {
        this.commandListenerThread.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (commandListenerThread.getLastCommand() != null) {
                    if (commandListenerThread.getLastCommand() instanceof NickCommand) {
                        Sounds.INCOMING.play();
                        frame.incomingCall(((NickCommand) commandListenerThread.getLastCommand()).getNick());
                        frame.addAcceptListener(new AcceptListener());
                        frame.addRejectListener(new RejectListener());
                        commandListenerThread.stop();
                    } else if (commandListenerThread.getLastCommand() instanceof MessageCommand) {
                        Sounds.RECEIVE.play();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                frame.appendMsg(((MessageCommand) commandListenerThread.getLastCommand()).getMessage());
                            }
                        });
                    } else if (commandListenerThread.getLastCommand() instanceof FileCommand) {
                        Sounds.RECEIVE.play();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                frame.appendMsg("Received file");
                            }
                        });
                    } else if (Checker.getType(commandListenerThread.getLastCommand().getType().toString()) != null) {
                        switch (commandListenerThread.getLastCommand().getType()) {
                            case ACCEPT: {
                                callListenerThread.setBusy(true);
                                break;
                            }
                            case REJECT: {
                                callListenerThread.setBusy(false);
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        frame.appendBroken("REJECTED");
                                        frame.setDisconnected();
                                    }
                                });
                                break;
                            }
                            case DISCONNECT: {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        frame.appendBroken("DISCONNECTED");
                                        frame.setDisconnected();
                                    }
                                });
                                callListenerThread.setBusy(false);
                                commandListenerThread = null;
                                connection = null;
                                break;
                            }
                        }
                    }
                } else {
                    commandListenerThread.stop();
                    connection = null;
                    commandListenerThread = null;
                    callListenerThread.setBusy(false);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            frame.appendBroken("CONNECTION BROKEN");
                            frame.setDisconnected();
                        }
                    });
                }
            }
        });
    }

    public void sending() {
        StringBuilder msg = new StringBuilder(frame.getMsg());
        if (msg.length() > Constants.MSG_LENGTH_MAX) {
            frame.setMsg(msg.delete(Constants.MSG_LENGTH_MAX, msg.length()).toString());
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (msg.length() > 0) {
                    frame.appendMyMsg(msg.toString());
                    Sounds.SEND.play();

                }
            }
        });
        connection.sendMsg(msg.toString());
    }

    // Listeners
    private class StateListener implements WindowStateListener {
        @Override
        public void windowStateChanged(WindowEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setLocations();
                }
            });
        }
    }

    private class FrameListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (server != null) {
                server.goOffline();
                server.disconnect();
            }
            super.windowClosing(e);
        }
    }

    private class ResizeListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setLocations();
                }
            });

        }
    }


    private class SendFileListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (frame.sendFileIsEnabled()) {
                frame.setMsg("Sending file...");
                frame.getTextWriteMsg().setEnabled(false);
                if (connection.sendFiles()) {
                    frame.setMsg("");
                    frame.getTextWriteMsg().setEnabled(true);
                    frame.appendMsg("File sent!");
                } else {
                    frame.appendMsg("File not sent!");
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (frame.sendFileIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setSendFileIcon(new ImageIcon(getClass().getResource("gui/frame/sendPrs.png")));
                    }
                });
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (frame.sendFileIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setSendFileIcon(new ImageIcon(getClass().getResource("gui/frame/sendEnt.png")));
                    }
                });
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (frame.sendFileIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setSendFileIcon(new ImageIcon(getClass().getResource("gui/frame/sendEnt.png")));
                    }
                });
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (frame.sendFileIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setSendFileIcon(new ImageIcon(getClass().getResource("gui/frame/sendIcon.png")));
                    }
                });
        }
    }

    private class BtnConListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (frame.conIsEnabled()) {
                // TODO CONNECTING
                if (frame.getRemoteAddress().length() > Constants.NICK_LENGTH_MIN) {
                    caller.setRemoteAddress(new InetSocketAddress(frame.getRemoteAddress(), Constants.PORT));
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            frame.cleanHist();
                        }
                    });
                    connection = caller.call();
                    if (connection == null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                frame.appendBroken("Couldn't connect ! The user may be offline");
                            }
                        });
                        Sounds.ERROR.play();
                    } else {
                        commandListenerThread = new CommandListenerThread(connection);
                        addCommandObserver();
                        frame.setRemoteNick(caller.getRemoteNick());
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                frame.setConnected();
                            }
                        });
                        commandListenerThread.start();
                        addCommandObserver();
                    }
                } else {
                    // TODO SHOW A MESSAGE TO FILL THE REMOTE ADDRESS FIELD
                }
            } else
                Sounds.DISABLED.play();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (frame.conIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setConnectIcon(new ImageIcon(getClass().getResource("gui/frame/conPrs.png")));
                    }
                });
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (frame.conIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setConnectIcon(new ImageIcon(getClass().getResource("gui/frame/conEnt.png")));
                    }
                });
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (frame.conIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setConnectIcon(new ImageIcon(getClass().getResource("gui/frame/conEnt.png")));
                    }
                });
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (frame.conIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setConnectIcon(new ImageIcon(getClass().getResource("gui/frame/conIcon.png")));
                    }
                });
        }
    }

    private class BtnApplyListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (frame.applyIsEnabled()) {
                // TODO CREATING Caller, STARTING CallListenerThread
                if (frame.getLocalNick().length() > Constants.NICK_LENGTH_MIN) {
                    try {
                        if (frame.getLocalNick().charAt(0) != ' ') {
                            caller = new Caller(frame.getLocalNick());
                            callListenerThread = new CallListenerThread(new CallListener(frame.getLocalNick()));
                            callListenerThread.addObserver(new Observer() {
                                @Override
                                public void update(Observable o, Object arg) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            connection = callListenerThread.getLastCon();
                                            commandListenerThread = new CommandListenerThread(connection);
                                            addCommandObserver();
                                            commandListenerThread.start();
                                        }
                                    });
                                }
                            });
                            Thread t = new Thread(callListenerThread);
                            t.setDaemon(true);
                            t.start();
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    frame.setApplied();
                                }
                            });


                            URL myIp = new URL("http://checkip.amazonaws.com");
                            BufferedReader in = new BufferedReader(new InputStreamReader(
                                    myIp.openStream()));
                            String ip = in.readLine();
                            in.close();


                            server = new ServerConnection("jdbc:mysql://files.litvinov.in.ua/chatapp_server?" +
                                    "characterEncoding=utf-8&useUnicode=true", frame.getLocalNick());
                            server.connect();
                            server.goOnline();
                            allConts = ContactsServer.readServer(server, frame.getLocalNick());
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < allConts.size(); i++) {
                                        frame.addServerContact(allConts.get(i));
                                        allConts.get(i).addMouseListener(new ContactListener());
                                    }

                                }
                            });

                            ContactsFile.checkFile();
                            locConts = ContactsFile.readFile(server);
                            if (locConts != null) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < locConts.size(); i++) {
                                            frame.addLocalContact(locConts.get(i));
                                            locConts.get(i).addMouseListener(new ContactListener());
                                        }
                                    }

                                });
                            }
                        } else {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    frame.appendBroken("Nick cannot begin with 'space' char");
                                }
                            });
                        }
                    } catch (UnknownHostException e2) {
                        e2.printStackTrace();
                    } catch (IOException e3) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                frame.appendBroken("Couldn't bind port !");
                            }
                        });
                        Sounds.ERROR.play();
                    }
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            frame.appendBroken("Length = >3 & <16");
                        }
                    });
                }
            } else {
                Sounds.DISABLED.play();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (frame.applyIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setApplyIcon(new ImageIcon(getClass().getResource("gui/frame/applyPrs.png")));
                    }
                });
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (frame.applyIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setApplyIcon(new ImageIcon(getClass().getResource("gui/frame/applyEnt.png")));
                    }
                });
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (frame.applyIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setApplyIcon(new ImageIcon(getClass().getResource("gui/frame/applyEnt.png")));
                    }
                });
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (frame.applyIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setApplyIcon(new ImageIcon(getClass().getResource("gui/frame/applyIcon.png")));
                    }
                });
        }
    }

    private class BtnDisconListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (frame.disconIsEnabled()) {
                // TODO DISCONNECTING
                commandListenerThread.stop();
                connection.send(Command.CommandType.DISCONNECT.toString());
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setDisconnected();
                    }
                });
            } else
                Sounds.DISABLED.play();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (frame.disconIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setDisconnectIcon(new ImageIcon(getClass().getResource("gui/frame/disconPrs.png")));
                    }
                });
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (frame.disconIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setDisconnectIcon(new ImageIcon(getClass().getResource("gui/frame/disconEnt.png")));
                    }
                });
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (frame.disconIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setDisconnectIcon(new ImageIcon(getClass().getResource("gui/frame/disconEnt.png")));
                    }
                });
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (frame.disconIsEnabled())
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setDisconnectIcon(new ImageIcon(getClass().getResource("gui/frame/disconIcon.png")));
                    }
                });
        }
    }

    private class BtnUserAddListener implements MouseListener {


        @Override
        public void mouseClicked(MouseEvent e) {
            if (frame.userAddIsEnabled()) {
                Contact addContact = new Contact(frame.getRemoteNick(), server.getIpForNick(frame.getRemoteNick()), true);
                if (locConts == null)
                    locConts = new ArrayList<>();
                locConts.add(addContact);
                ContactsFile.writeFile(locConts);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.addLocalContact(addContact);
                    }
                });
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (frame.userAddIsEnabled()) {
                frame.setUserAddIcon(new ImageIcon(getClass().getResource("gui/frame/userAddPrs.png")));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (frame.userAddIsEnabled()) {
                frame.setUserAddIcon(new ImageIcon(getClass().getResource("gui/frame/userAddEnt.png")));
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (frame.userAddIsEnabled()) {
                frame.setUserAddIcon(new ImageIcon(getClass().getResource("gui/frame/userAddEnt.png")));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (frame.userAddIsEnabled()) {
                frame.setUserAddIcon(new ImageIcon(getClass().getResource("gui/frame/userAddIcon.png")));
            }
        }
    }

    private class AcceptListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            acceptIncomingCall();
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setConnected();
                        frame.dialogSetVisible(false);
                    }
                });
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setAcceptIcon(new ImageIcon(getClass().getResource("gui/dialog/acPrs.png")));
                    }
                });
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setAcceptIcon(new ImageIcon(getClass().getResource("gui/dialog/acEnt.png")));
                    }
                });
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setAcceptIcon(new ImageIcon(getClass().getResource("gui/dialog/acEnt.png")));
                    }
                });
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setAcceptIcon(new ImageIcon(getClass().getResource("gui/dialog/acIcon.png")));
                    }
                });
            }
        }
    }

    private class RejectListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (frame.incomingVisible()) {
                try {
                    rejectIncomingCall();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            frame.dialogSetVisible(false);
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setRejectIcon(new ImageIcon(getClass().getResource("gui/dialog/rejPrs.png")));
                    }
                });
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setRejectIcon(new ImageIcon(getClass().getResource("gui/dialog/rejEnt.png")));
                    }
                });
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setRejectIcon(new ImageIcon(getClass().getResource("gui/dialog/rejEnt.png")));
                    }
                });
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (frame.incomingVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setRejectIcon(new ImageIcon(getClass().getResource("gui/dialog/rejIcon.png")));
                    }
                });
            }
        }
    }

    private class NickTextListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            StringBuilder b = new StringBuilder(frame.getLocalNick());
            if (b.length() > Constants.NICK_LENGTH_MAX) {
                frame.setLocalNick(b.delete(Constants.NICK_LENGTH_MAX, b.length()).toString());
            }
            if (e.getKeyChar() == '\n') {
                frame.setLocalNick(b.toString().trim());
            }
        }
    }

    private class RemoteAddressListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    private class WriteMsgListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            StringBuilder b = new StringBuilder(frame.getMsgText());
            if (b.length() > Constants.MSG_LENGTH_MAX) {
                frame.setMsg(b.delete(Constants.MSG_LENGTH_MAX, b.length()).toString());
            }
            if (e.getKeyChar() == '\n') {
                sending();
            }
        }
    }

    private class ContactListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Contact c = (Contact) e.getSource();
            caller.setRemoteAddress(new InetSocketAddress(c.getAddr(), Constants.PORT));
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.cleanHist();
                }
            });
            connection = caller.call();
            if (connection == null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.appendBroken("Couldn't connect ! The user may be offline");
                    }
                });
                Sounds.ERROR.play();
            } else {
                commandListenerThread = new CommandListenerThread(connection);
                addCommandObserver();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setRemoteNick(caller.getRemoteNick());
                        frame.setRemoteAddress(c.getAddr());
                        frame.setConnected();
                        frame.revalidate();
                    }
                });
                commandListenerThread.start();
                addCommandObserver();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            ((Contact) e.getSource()).setBackground(Colors.dark0);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            ((Contact) e.getSource()).setBackground(Colors.dark2);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            ((Contact) e.getSource()).setBackground(Colors.dark1);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            ((Contact) e.getSource()).setBackground(Colors.dark2);
        }
    }

}
