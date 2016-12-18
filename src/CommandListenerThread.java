import java.io.IOException;
import java.util.Observable;

/*

CLASS TO RECEIVE COMMANDS THAT HELPS TO UNDERSTAND WHICH STEP TO MAKE NEXT

 */

public class CommandListenerThread extends Observable implements Runnable {
    private boolean disconnected;
    private Connection connection;
    private Command lastCommand;

    public CommandListenerThread(Connection connection) {
        this.connection = connection;
        this.lastCommand = new Command();
    }


    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public Command getLastCommand() {
        return this.lastCommand;
    }

    public void start() {
        this.disconnected = false;
        Thread commandThread = new Thread(this);
        commandThread.start();
    }

    public void stop() {
        this.disconnected = true;
    }

    @Override
    public void run() {
        while (disconnected == false) {
            String command = connection.receive();
            if (command != null) {
                this.lastCommand = new Command(Checker.getType(command));
                if (this.lastCommand.getType() != null) {
                    switch (this.lastCommand.getType()) {
                        case ACCEPT: {
                            this.disconnected = false;
                            break;
                        }
                        case DISCONNECT: {
                            try {
                                this.disconnected = true;
                                this.connection.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            this.disconnected = true;
                            break;
                        }
                        case REJECT: {
                            this.disconnected = true;
                            break;
                        }
                        default: {
                            connection.send(Command.CommandType.REJECT.toString());
                            this.disconnected = true;
                            break;
                        }
                    }
                    setChanged();
                    notifyObservers();
                    clearChanged();
                } else if (command.equals("Message")) {
                    this.lastCommand = new MessageCommand();
                    ((MessageCommand) this.lastCommand).setMessage(connection.receive());
                    setChanged();
                    notifyObservers();
                    clearChanged();
                } else if (command.equals("File")) {
                    this.lastCommand = new FileCommand();
                    ((FileCommand) this.lastCommand).setReceived(connection.receiveFile());
                    setChanged();
                    notifyObservers();
                    clearChanged();
                } else {
                    if (command.substring(0, 7).equals("ChatApp")) {
                        String[] info = Checker.getInfo(command);
                        if (info != null) {
                            if (info.length == 2) {
                                this.lastCommand = new NickCommand(false, info[0], info[1]);
                                setChanged();
                                notifyObservers();
                                clearChanged();
                            } else {
                                this.lastCommand = new NickCommand(true, info[0], info[1]);
                            }
                        } else {
                            connection.send(Command.CommandType.REJECT.toString());
                        }
                    } else connection.send(Command.CommandType.REJECT.toString());
                }
            } else {
                this.lastCommand = null;
                setChanged();
                notifyObservers();
                clearChanged();
            }
        }
    }

}
