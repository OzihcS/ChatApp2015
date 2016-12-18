
public class FileCommand extends Command {
    private boolean isReceived;

    public FileCommand() {
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }
}
