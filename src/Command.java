
/*

CLASS THAT CONTAINS ALL THE COMMANDS
WARNING! THERE ARE OTHER CLASSES THAT EXTENDS THIS ONE

 */

public class Command {

    private CommandType type;

    public Command(CommandType type) {
        this.type = type;
    }

    public Command() {
    }

    public CommandType getType() {
        return this.type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public enum CommandType {
        ACCEPT {
            @Override
            public String toString() {
                return "Accepted";
            }
        },
        DISCONNECT {
            @Override
            public String toString() {
                return "Disconnect";
            }
        },
        REJECT {
            @Override
            public String toString() {
                return "Rejected";
            }
        }
    }
}
