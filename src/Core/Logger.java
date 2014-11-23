package Core;

import java.io.*;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created by Savonin on 2014-11-15
 */
public class Logger {

	private static int FLUSH_LIMIT = 100;

	/**
	 * @param log - String to log
	 */
	public synchronized void log(String log) throws IOException, InternalError {
		String strTime = String.format("%s:%s:%s",
				twoSymbols(calendar.get(Calendar.HOUR_OF_DAY)),
				twoSymbols(calendar.get(Calendar.MINUTE)),
				twoSymbols(calendar.get(Calendar.SECOND))
		);
		if (logVector.add(strTime + " [" + log + "]\n") && logVector.size() >= FLUSH_LIMIT) {
			flush();
		}
	}

	/**
	 * @return - Logger with logged date for current day
	 */
	public static synchronized Vector<String> loadLog() {
		return loadLog(Logger.getLogger().todayFileName());
	}

	/**
	 * @param usDate - Date
	 * @return - Logger with logged data
	 */
	public static synchronized Vector<String> loadLog(String usDate) {
		Vector<String> stringVector = new Vector<String>();
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(Config.LOG_PATH + usDate + ".txt")
			);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringVector.add(line + "\n");
			}
		} catch (FileNotFoundException ignored) {
		} catch (IOException ignored) {
		}
		return stringVector;
	}

	/**
	 * @param number - Integer number
	 * @return - Formatted string
	 */
	private String twoSymbols(int number) {
		if (number < 10) {
			return "0" + Integer.toString(number);
		}
		return Integer.toString(number);
	}

	/**
	 * @return - Today's filename
	 */
	public String todayFileName() {
		return String.format("%d-%s-%s", calendar.get(Calendar.YEAR),
				twoSymbols(calendar.get(Calendar.MONTH) + 1),
				twoSymbols(calendar.get(Calendar.DAY_OF_MONTH))
		);
	}

	/**
	 * Flush all logs
	 */
	private synchronized void flush() throws InternalError, IOException {
		File logHandle = new File(Config.LOG_PATH + todayFileName() + ".txt");
		if (!logHandle.exists() && !logHandle.createNewFile()) {
			throw new InternalError("Unable to create log file");
		}
		FileWriter fileWriter = new FileWriter(
				logHandle, true
		);
		try {
			for (String s : logVector) {
				fileWriter.write(s);
			}
			fileWriter.flush();
		} catch (Exception e) {
			throw new InternalError(e.getMessage());
		}
		logVector.clear();
	}

	/**
	 * @return - Vector with logs
	 */
	public Vector<String> getLogVector() {
		return logVector;
	}

	/**
	 * Locked
	 */
	private Logger() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					flush();
				} catch (Throwable ignored) {
				}
			}
		});
	}

	private Vector<String> logVector
			= new Vector<String>();

	/**
	 * @return - Logger instance
	 */
	public static Logger getLogger() {
		return logger;
	}

	private static Logger logger
			= new Logger();

	private Calendar calendar
			= Calendar.getInstance();
}
