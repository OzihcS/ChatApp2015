import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class LogService {
    private static String LOG_DIR = "log";
    private static String EXTENSION = ".log";

    private PrintStream standardPrintStream;
    private PrintStream customPrintStream;
    private PrintStream standardErrorPrintStream;
    private PrintStream customErrorPrintStream;

    public LogService() throws IOException {
        String baseLogFileName = String.valueOf(System.currentTimeMillis());
        String outLogFileName = LOG_DIR + "/" + baseLogFileName + EXTENSION;
        String errLogFileName = LOG_DIR + "/" + baseLogFileName + "_err" + EXTENSION;
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        createLogFiles(outLogFileName, errLogFileName);
    }

    private void createLogFiles(String outLogFileName, String outErrLogFileName) throws IOException {
        standardPrintStream = System.out;
        File outFile = new File(outLogFileName);
        outFile.createNewFile();
        customPrintStream = new PrintStream(new File(outLogFileName));

        standardErrorPrintStream = System.err;
        File outErrFile = new File(outErrLogFileName);
        outErrFile.createNewFile();
        customErrorPrintStream = new PrintStream(outErrFile);
    }

    public void start() throws FileNotFoundException {
        System.setOut(customPrintStream);
        System.setErr(customErrorPrintStream);
    }

    public void end() {
        System.setOut(standardPrintStream);
        System.setErr(standardErrorPrintStream);
        customPrintStream.close();
        customErrorPrintStream.close();
    }
}
