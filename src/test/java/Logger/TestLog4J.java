package Logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Logging.Log4J;
import org.junit.Test;

public class TestLog4J {

    final static Logger logger = LogManager.getLogger(TestLog4J.class.getName());

    static {
        Log4J.enableForClass(TestLog4J.class);
    }

    @Test
    public static void testDebug() {
        logger.debug("Hello this is a debug message");
        System.out.println("Print In Log File");
        logger.info("Hello this is a info message");
    }
}