import java.io.IOException;
import java.util.Observable;

/*

CLASS THAT IS LOOKING FORWARD FOR AN INCOMING CONNECTION

 */
public class CallListenerThread extends Observable implements Runnable {
    private CallListener callListener;

    public CallListenerThread(CallListener callListener) {
        this.callListener = callListener;
    }

    @Override
    public void run() {
        while (Thread.currentThread().isAlive()) {
            try {
                Connection connection = callListener.getConnection();
                if (callListener.isBusy()) {
                    connection.sendNick(Constants.DEFAULT_VER, callListener.getLocalNick(), true);
                } else {
                    connection.sendNick(Constants.DEFAULT_VER, callListener.getLocalNick(), false);
                    setChanged();
                    notifyObservers();
                    clearChanged();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setBusy(boolean busy) {
        this.callListener.setBusy(busy);
    }

    public Connection getLastCon() {
        return this.callListener.getLastCon();
    }
}
