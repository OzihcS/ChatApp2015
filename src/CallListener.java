/*

CLASS THAT IS USED BY <CallListenerThread> TO HANDLE INCOMING CONNECTIONS

 */

import java.io.IOException;
import java.net.*;

public class CallListener {
    private boolean busy;
    private String localNick;
    private Connection lastCon;
    private ServerSocket serverSocket;

    public CallListener(String localNick) throws IOException {
        this.serverSocket = new ServerSocket(Constants.PORT);
        this.localNick = localNick;
        this.busy = false;
    }

    public Connection getConnection() throws IOException {
        Socket socket = serverSocket.accept();
        lastCon = new Connection(socket);
        return lastCon;
    }

    public boolean isBusy() {
        return this.busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public Connection getLastCon() {
        return this.lastCon;
    }

    public String getLocalNick() {
        return this.localNick;
    }

}
