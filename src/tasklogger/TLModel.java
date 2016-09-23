package tasklogger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;

/**
 * @author Nathan
 * 
 */
public class TLModel {

	private static ArrayList<TLTask> taskArray;
	private static TLModel instance;
	private static PropertyChangeSupport pcs;
	private final static String ROOT_FILE_NAME = "tasklogger";

	private enum CsvFormat {
		TASKNAME, TIME_IN_MS, CSV_TIME_IS_HMS
	}

	private TLModel() {
		taskArray = new ArrayList<>();
		pcs = new PropertyChangeSupport(this);
	}

	public static void addModelToView() {
		TLView.setTotalTimerInMs(TLTask.getTotalRunTimeInMs());
		for (TLTask t : taskArray) {
			TLView.addTask(t.getTaskID());
		}
	}

	public static TLModel getInstance() {
		if (instance == null) {
			instance = new TLModel();
		}
		return (instance);
	}

	private static TLTask getTaskWithID(int taskID) {
		for (TLTask t : taskArray) {
			if (t.getTaskID() == taskID) {
				return (t);
			}
		}
		return null;
	}

	public static TLTask newTask(final String inName) {
		// Exit early 
		if (null == inName) {
			return(null);
		}
		
		// Find task in arrayTask
		for (TLTask t : taskArray) {
			if (t.getName().equals(inName)) {
				return (null);
			}
		}

		TLTask task = new TLTask(inName);
		pcs.firePropertyChange("taskAction:" + task.getTaskID(), task.getRunning().booleanValue(), 0);
		taskArray.add(task);
		return (task);
	}

	/**
	 * 
	 * @param taskID
	 */
	public static void tasktButtonPressed(int taskID) {
		TLTask task = getTaskWithID(taskID);

		// Toggle activeTask
		if ((TLTask.getActiveTask() != null)
				&& (TLTask.getActiveTask() != task)
				&& (TLTask.getActiveTask().getRunning())) {
			final int activeTaskID = TLTask.getActiveTask().getTaskID();
			TLTask.getActiveTask().actionTask();
			pcs.fireIndexedPropertyChange("taskStateChange", activeTaskID,
					true, false);
		}

		Boolean before = task.getRunning();
		task.actionTask();
		pcs.fireIndexedPropertyChange("taskStateChange", task.getTaskID(),
				before, task.getRunning());
	}

	public static void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public static void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	public static String getTaskName(int inTaskID) {
		return (getTaskWithID(inTaskID).getName());
	}

	public static void writeTaskTimesToFile() {
		// Print
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		System.out.println(TLUtilities.getHMSString(TLTask
				.getTotalRunTimeInMs()) + " << Total time");
		for (TLTask t : taskArray) {
			System.out.println(TLUtilities.getHMSString(t.getActiveTimeInMs())
					+ " : " + t.getName());
		}
		System.out.println();
		try {
			exportCVSFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Save times error",
					"Could not write to file.", JOptionPane.WARNING_MESSAGE);
		}
	}

	public static void setTaskName(int taskID, String taskName) {
		TLTask t = getTaskWithID(taskID);
		if (t != null) {
			t.setTitle(taskName);
		}
	}

	public static void exportCVSFile() throws IOException {		
		String fileName = getDataLogFile();

		FileWriter writer;
		writer = new FileWriter(fileName);
		long timeValue = TLTask.getTotalRunTimeInMs();
		writer.append("Task,ms,HHmmss\n");
		writer.append("Total," + timeValue + ","
				+ TLUtilities.getHMSString(timeValue) + "\n");

		// Pad the name to align the ','
		int maxNameLength = 0;
		for (TLTask t : taskArray) {
			if (t.getName().length() > maxNameLength) {
				maxNameLength = t.getName().length();
			}
		}
		String padString = "%0$-" + maxNameLength + "s";

		for (TLTask t : taskArray) {
			timeValue = t.getActiveTimeInMs();
			writer.append(String.format(padString, t.getName()) + "," + timeValue + ","
					+ TLUtilities.getHMSString(timeValue) + "\n");
		}

		writer.close();
	}

	private static String getDataLogFile() {
		String fileName = "C:/tmp/" + ROOT_FILE_NAME + "_" + TLUtilities.getToday()
				+ ".csv";
		return(fileName);
	}

	public static void importCSVFile() throws FileNotFoundException,
	IOException {
		String fileName = getDataLogFile();
		File f = new File(fileName);
		if (f.exists() && !f.isDirectory()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine(); // Header
			line = br.readLine(); // Total times
			if (null != line) {
				String[] input = line.split(",");
				TLTask.setTotalTime(Long.parseLong(input[CsvFormat.TIME_IN_MS
				                                         .ordinal()]));
				while ((line = br.readLine()) != null) {
					// Task times
					input = line.split(",");
					TLTask t = new TLTask(
							input[CsvFormat.TASKNAME.ordinal()],
							Long.parseLong(input[CsvFormat.TIME_IN_MS.ordinal()]));
					taskArray.add(t);
				}
			}
			br.close();			
		}
	}

	public static void deleteTask(int taskID) {
		TLController.deleteTask(taskID);
		TLTask t = getTaskWithID(taskID);
		TLTask.setTotalTime(TLTask.getTotalRunTimeInMs()
				- t.getActiveTimeInMs());
		taskArray.remove(t);
		t = null;

	}

	public static String getTaskTimeWithID(int taskID) {
		final TLTask t = getTaskWithID(taskID);
		return (TLUtilities.getHMSString(t.getActiveTimeInMs()));
	}

	public static void reset() {
		int dialogResult = JOptionPane.showConfirmDialog(null,
				"Reset all tasks?", "Reset TaskLogger",
				JOptionPane.YES_NO_OPTION);
		if (dialogResult != JOptionPane.YES_OPTION) {
			return;
		}

		for (TLTask t : taskArray) {
			setActiveTimeInMs(t.getTaskID(), 0);
		}
		setTotalRunTimeInMs(0);
	}

	public static void setTotalRunTimeInMs(long timeInMs) {
		final long before = TLTask.getTotalRunTimeInMs();
		TLTask.setTotalTime(timeInMs);
		pcs.firePropertyChange("totalRunTimeInMs", before,
				TLTask.getTotalRunTimeInMs());
	}

	public static void setActiveTimeInMs(int taskID, long timeInMs) {
		TLTask t = getTaskWithID(taskID);
		final long before = t.getActiveTimeInMs();
		t.setActiveTimeInMs(timeInMs);
		pcs.fireIndexedPropertyChange("activeTimeInMs", taskID, before,
				t.getActiveTimeInMs());
	}

}
