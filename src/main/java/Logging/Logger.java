package Logging;

import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Logger extends Thread{
    private final String logLocation = System.getProperty("user.dir");
    private String logName;
    private BlockingQueue<LogEntry> queue;
    public Logger(BlockingQueue<LogEntry> queue){
        this.queue = queue;
        logName = "log-" + getCurrentTimeUsingDate() + ".txt";
    }

    private static String getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "yyyy,MM,dd";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    private void logToFile(LogEntry entry) throws IOException {
        String fileContent = "\n"+entry.toString();
        BufferedWriter writer = new BufferedWriter(new FileWriter(logLocation + "/" + logName));
        writer.write(fileContent);
        writer.close();
    }

    @Override
    public void run(){
        while (true) {
            try {
                LogEntry data = queue.take();
                //handle the data
                try {
                    logToFile(data);
                }catch(IOException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Unable to write to the log. Errors will not be reported.");

                    alert.showAndWait();
                }
            } catch (InterruptedException e) {
                System.err.println("Error occurred:" + e);
            }
        }
    }
}
