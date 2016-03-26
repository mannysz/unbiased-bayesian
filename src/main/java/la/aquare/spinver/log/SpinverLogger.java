package la.aquare.spinver.log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpinverLogger {
	private static final Logger consoleLogger =
	        Logger.getLogger(SpinverLogger.class.getName() + "-console");
	private static final Logger fileLogger =
	        Logger.getLogger(SpinverLogger.class.getName() + "-file");
	private static final Logger rejectedLogger =
	        Logger.getLogger(SpinverLogger.class.getName() + "-rejected");
	
	private static String logFilePrefix = null;
	
	private static ConsoleHandler consoleHandler = null;
	private static FileHandler handler = null, rejectedHandler = null;
	
	private static int logRotateCount = 10;          //10 arquivos de log no máximo
	private static int logSizeLimit = 5 * (1024*1024);  //5 MB
	private static int rejectedLogSizeLimit = 100 * (1024*1024); //100 MB
	
	public static void init(String dirname, boolean toFile) {
		LogFormatter formatter = new LogFormatter();
		consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(formatter);
		consoleLogger.addHandler(consoleHandler);
		consoleLogger.setUseParentHandlers(false);
		if (toFile) {
			logFilePrefix = dirname.replace("\"", "").replace("\'", "") +
					"/spinver_" + System.currentTimeMillis();
			try {
				handler = new FileHandler(logFilePrefix  + ".%g.log",
						logSizeLimit, logRotateCount, true);
				rejectedHandler =  new FileHandler(logFilePrefix  + "_rejected.%g.log", 
						rejectedLogSizeLimit, logRotateCount, true);
			} catch (SecurityException e) {
				consoleLogger.log(Level.SEVERE, "Error: ", e);
			} catch (IOException e) {
				consoleLogger.log(Level.SEVERE, "Error: ", e);
			}
			handler.setFormatter(new LogFormatter());
			rejectedHandler.setFormatter(formatter);
			fileLogger.addHandler(handler);
			rejectedLogger.addHandler(rejectedHandler);
			fileLogger.setUseParentHandlers(false);
			rejectedLogger.setUseParentHandlers(false);
		}		
	}
	
	public static void logToScreen(String msg) {
		consoleLogger.log(Level.INFO, msg);
	}
	
	public static void logToScreen(Throwable t) {
		consoleLogger.log(Level.SEVERE, "Error: ", t);
	}

	public static void log(String msg) {
		fileLogger.log(Level.INFO, msg);
	}
	
	public static void logRejected(String msg) {
		rejectedLogger.log(Level.INFO, msg);
	}	
	
	public static String getLogFileName() {
		return logFilePrefix;
	}
}
