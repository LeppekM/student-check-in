package Logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Logging.Log4J;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TestLog4J {

    final static Logger logger = LogManager.getLogger(TestLog4J.class.getName());

    static {
        Log4J.enableForClass(TestLog4J.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.setProperty("current.date", dateFormat.format(new Date()));
    }

    @Before
    public void createLogFile() {
        logger.debug("Hello this is a debug message");
        logger.error("This is an error message");
        logger.warn("This is a warning");
        logger.info("Hello this is a info message");
    }

    @Test
    public void checkLogFileExists(){
        File file = new File(System.getProperty("user.dir") + "\\latest.log");
        assertEquals(file.exists(), true);
        assertEquals(file.isDirectory(), false);
    }
}