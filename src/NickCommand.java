/*

CLASS STORES INFORMATION ABOUT THE PAL YOU ARE CONNECTED AT THE MOMENT

 */

public class NickCommand extends Command {

    private boolean isBusy;
    private String ver;
    private String nick;

    public NickCommand(boolean isBusy, String ver, String nick) {
        this.isBusy = isBusy;
        this.ver = ver;
        this.nick = nick;
    }

    public void setBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean getBusy() {
        return this.isBusy;
    }

    public String getVer() {
        return this.ver;
    }

    public String getNick() {
        return this.nick;
    }
}
