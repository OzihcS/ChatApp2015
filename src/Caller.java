import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/*

CLASS FOR OUTGOING CONNECTIONS

 */

public class Caller {
    private CallStatus status;
    private String localNick;
    private String remoteNick;
    private SocketAddress remoteAddress;

    public Caller(String localNick) {
        this.localNick = localNick;
    }

    public Connection call() {
        try {
            Socket socket = new Socket();
            socket.connect(this.remoteAddress, 30000);
            Connection connection = new Connection(socket);
            String firstMsg = connection.receive();
            if (firstMsg.equals(Command.CommandType.REJECT.toString())) {
                connection.close();
                this.status = CallStatus.REJECTED;
                return null;
            } else {
                String[] info = Checker.getInfo(firstMsg);
                if (info != null) {
                    if (info.length == 3) {
                        this.status = CallStatus.BUSY;
                        connection.close();
                        return null;
                    } else {
                        this.remoteNick = info[1];
                        this.status = CallStatus.OK;
                        connection.sendNick(Constants.DEFAULT_VER, this.localNick, false);
                        return connection;
                    }
                } else {
                    connection.send(Command.CommandType.DISCONNECT.toString());
                    connection.close();
                    return null;
                }
            }
        } catch (IOException e) {
            this.status = CallStatus.NOT_ACCESIBLE;
            e.printStackTrace();
            return null;
        }
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getRemoteNick() {
        return this.remoteNick;
    }

    public CallStatus getStatus() {
        return this.status;
    }

    public enum CallStatus {
        NOT_ACCESIBLE, BUSY, OK, REJECTED, NO_SERVICE;
    }
}
