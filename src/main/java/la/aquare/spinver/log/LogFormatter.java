package la.aquare.spinver.log;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formato de {@link LogRecord}:
 * <pre>data   n&iacute;vel   mensagem</pre>.
 */
public class LogFormatter extends Formatter {
	private boolean showDate = false;
	
	
	public LogFormatter(boolean showDate_) {
		super();
		showDate = showDate_;
	}
	
	public LogFormatter() {
		this(false);
	}


	public String format(LogRecord record) {
		Date date = new Date(record.getMillis());
		//return date.toString() + " " + record.getLevel().getName() + " " + formatMessage(record) + " \n";
		return (showDate?date.toString() + " ":"") + formatMessage(record).replace("\n", "\\n") + " \n";
	}
	
	public static String getNow() {
		return new Date(System.currentTimeMillis()).toString();
	}
}