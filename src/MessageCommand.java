/*

CLASS STORES <STRING> WITH THE LAST MESSAGE YOU'VE RECEIVED

 */
public class MessageCommand extends Command {
    private String message;

    public MessageCommand() {
    }

    public MessageCommand(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
