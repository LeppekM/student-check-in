package Logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;

public class Log4J extends PrintStream {

    private static final PrintStream originalSystemOut = System.out;
    private static Log4J systemOutToLogger;

    @SuppressWarnings("rawtypes")
    public static void enableForClass(Class className) {
        systemOutToLogger = new Log4J(originalSystemOut, className.getName());
        System.setOut(systemOutToLogger);
    }

    public static void enableForPackage(String packageToLog) {
        systemOutToLogger = new Log4J(originalSystemOut, packageToLog);
        System.setOut(systemOutToLogger);
    }

    public static void disable() {
        System.setOut(originalSystemOut);
        systemOutToLogger = null;
    }

    private final String packageOrClassToLog;
    private Log4J(PrintStream original, String packageOrClassToLog) {
        super(original);
        this.packageOrClassToLog = packageOrClassToLog;
    }

    @Override
    public void println(String line) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = findCallerToLog(stack);
        if (caller == null) {
            super.println(line);
            return;
        }

        Logger logger = LogManager.getLogger(caller.getClass());
        logger.debug("Code Line No.: " + stack[2].getLineNumber() + ", Class Name: " + caller.getClassName() + ", Text: " + line);
    }

    public StackTraceElement findCallerToLog(StackTraceElement[] stack) {
        for (StackTraceElement element : stack) {
            if (element.getClassName().startsWith(packageOrClassToLog)) {
                return element;
            }
        }
        return null;
    }
}