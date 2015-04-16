package ch.jamiete.bunbun;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormat extends Formatter {
    private final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD kk:mm:ss");

    @Override
    public String format(LogRecord record) {
        String log = "";

        log += sdf.format(new Date(record.getMillis()));
        log += " [" + record.getLevel().getLocalizedName().toUpperCase() + "]";

        if (record.getSourceClassName() != null) {
            String[] split = record.getSourceClassName().split("\\.");
            log += " [" + split[split.length == 1 ? 0 : (split.length - 1)] + "]";
            //log += " [" + record.getSourceClassName() + "]";
        }

        log += " " + record.getMessage();

        log += System.getProperty("line.separator");

        return log;
    }
}
