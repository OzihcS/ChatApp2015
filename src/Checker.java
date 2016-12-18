/*

CLASS CONTAINS METHODS THAT CHECKS RECEIVED COMMANDS, RECEIVED "HELLO MESSAGE", ETC

 */

public class Checker {
    // Method that checks if the received type of Command exists in the list
    public static Command.CommandType getType(String fromString) {
        Command.CommandType tmp = null;
        for (Command.CommandType t : Command.CommandType.values()) {
            if (t.toString().equals(fromString))
                tmp = t;
        }
        return tmp;
    }

    // Method that checks if the received "hello message" is right
    public static String[] getInfo(String gotInfo) {
        String[] info = gotInfo.split(" ");

        /*

        Right line :
        info[0] = "ChatApp"
        info[1] = "2015"
        info[2] = "user"
        info[3] = nickname of the pal (length >=3 & <=12) (at this one word);
        info[length-1] = "busy" - can be missed if the user if free

         */

        if ((info.length <= 5) & info[0].equals("ChatApp") & info[1].equals("2015") & info[2].equals("user")) {
            if (info[info.length - 1].equals("busy")) {
                String[] rightInfo = new String[3];
                StringBuilder tmp = new StringBuilder(info[0] + " " + info[1]);
                rightInfo[0] = tmp.toString();
                rightInfo[1] = info[3];
                rightInfo[2] = info[4];
                return rightInfo;
            } else {
                String[] rightInfo = new String[2];
                StringBuilder tmp = new StringBuilder(info[0] + " " + info[1]);
                rightInfo[0] = tmp.toString();
                rightInfo[1] = info[3];
                return rightInfo;
            }
        } else return null;
    }
}
