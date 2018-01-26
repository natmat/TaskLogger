package tasklogger;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
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

import tasklogger.TLUtilities.PlatformOS;

/**
 * @author Nathan
 * 
 */
public class TLModel implements PropertyChangeListener {

	private static ArrayList<TLTask> taskArray;
	private static TLModel instance;
	private static PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(getInstance());
	private final static String ROOT_FILE_NAME = "tasklogger";

	private enum CsvFormat {
		TASKNAME, TIME_IN_MS, TIME_IS_HMS
	}

	private TLModel() {
		taskArray = new ArrayList<>();
		propertyChangeSupport = new PropertyChangeSupport(this);
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
			return (null);
		}

		// Find task in arrayTask
		for (TLTask t : taskArray) {
			if (t.getName().equals(inName)) {
				return (null);
			}
		}

		TLTask task = new TLTask(inName);
		propertyChangeSupport.firePropertyChange("taskAction:" + task.getTaskID(), task.getRunning().booleanValue(), 0);
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
		if ((TLTask.getActiveTask() != null) && (TLTask.getActiveTask() != task)
				&& (TLTask.getActiveTask().getRunning())) {
			final int activeTaskID = TLTask.getActiveTask().getTaskID();
			TLTask.getActiveTask().actionTask();
			propertyChangeSupport.fireIndexedPropertyChange("taskStateChange", activeTaskID, true, false);
		}

		Boolean before = task.getRunning();
		task.actionTask();
		propertyChangeSupport.fireIndexedPropertyChange("taskStateChange", task.getTaskID(), before, task.getRunning());
	}

	public static void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	public static void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	public static String getTaskName(int inTaskID) {
		return (getTaskWithID(inTaskID).getName());
	}

	public static void writeTaskTimesToFile() {
		// Print
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		System.out.println(TLUtilities.getHMSString(TLTask.getTotalRunTimeInMs()) + " << Total time");
		for (TLTask t : taskArray) {
			System.out.println(TLUtilities.getHMSString(t.getActiveTimeInMs()) + " : " + t.getName());
		}
		System.out.println();
		try {
			exportCVSFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Save TaskLogger error", "Could not write to file.",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public static boolean setTaskName(int taskID, String taskName) {
		TLTask task = getTaskWithID(taskID);
		boolean canSetTaskName = false;
		if (null == task) {
			TLView.writeInfo("TaskID null");
		} else {
			if (taskNameUnique(taskName)) {
				task.setTitle(taskName);
				canSetTaskName = true;
			} else {
				TLView.writeInfo("Task " + taskName + " already exists");
			}
		}
		return (canSetTaskName);
	}

	private static boolean taskNameUnique(final String name) {
		for (TLTask t : taskArray) {
			if (t.getName().equals(name)) {
				return (false);
			}
		}
		return (true);
	}

	public static void exportCVSFile() throws IOException {
		String fileName = getTodaysCVSFileName();

		FileWriter writer;
		writer = new FileWriter(fileName);
		long timeValue = TLTask.getTotalRunTimeInMs();
		writer.append("Task,ms,HHmmss\n");
		writer.append("Total," + timeValue + "," + TLUtilities.getHMSString(timeValue) + "\n");

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
		TLView.writeInfo("CSV export complete");
	}

	private static String getTodaysCVSFileName() {
		String fileName = null;
		switch(TLUtilities.getOS()) {
		case MAC: 
			fileName = "/Users/Nathan/tmp/" + ROOT_FILE_NAME + "_" + TLUtilities.getToday();
			break;
		case WINDOWS:
			deafult:
			fileName = "C:/tmp/" + ROOT_FILE_NAME + "_" + TLUtilities.getToday();
		}
		return (fileName + ".csv");
	}

	/**
	 * Read in the dated CSV log file for the taskLogger and populate the taskArray
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void importTodaysCSVBackup() throws FileNotFoundException, IOException {
		final File f = new File(getTodaysCVSFileName());
		if (!f.exists() || f.isDirectory()) {
			TLView.writeInfo("No CSV model file for today");
			return;
		} 

		final BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine(); // header line
		line = br.readLine(); // totalTime
		if (null != line) {
			String[] taskLine = line.split(",");
			TLTask.setTotalTime(Long.parseLong(taskLine[CsvFormat.TIME_IN_MS.ordinal()]));

			// Read in CVS lines of tasks
			while (null != (line = br.readLine())) {
				taskLine = line.split(",");
				TLTask t = new TLTask(taskLine[CsvFormat.TASKNAME.ordinal()],
						Long.parseLong(taskLine[CsvFormat.TIME_IN_MS.ordinal()]));
				taskArray.add(t);
			}
		}
		System.out.println("importTodaysCSVBackup:" + taskArray);
		br.close();
	}

	public static void deleteTask(int taskID) {
		TLController.deleteTaskFormView(taskID);
		TLTask t = getTaskWithID(taskID);
		if (null != t) {
			TLTask.setTotalTime(TLTask.getTotalRunTimeInMs() - t.getActiveTimeInMs());
			taskArray.remove(t);
			TLView.writeInfo("Delete task " + t.getName());
		}
		t = null;
	}

	public static String getTaskTimeWithID(int taskID) {
		final TLTask t = getTaskWithID(taskID);
		return (TLUtilities.getHMSString(t.getActiveTimeInMs()));
	}

	public static void reset() {
		int dialogResult = JOptionPane.showConfirmDialog(null, "Reset all tasks?", "Reset TaskLogger",
				JOptionPane.YES_NO_OPTION);
		if (dialogResult != JOptionPane.YES_OPTION) {
			return;
		}

		setTotalRunTimeInMs(0);
		for (TLTask t : taskArray) {
			resetActiveTime(t.getTaskID());
		}
	}

	public static void setTotalRunTimeInMs(long timeInMs) {
		final long before = TLTask.getTotalRunTimeInMs();
		TLTask.setTotalTime(timeInMs);
		// No taskID to index for this change
		propertyChangeSupport.firePropertyChange("totalRunTimeInMs", before, TLTask.getTotalRunTimeInMs());
	}

	public static void resetActiveTime(int taskID) {
		final long resetTime = 0L;
		TLTask t = getTaskWithID(taskID);
		final long before = t.getActiveTimeInMs();
		t.setActiveTimeInMs(resetTime);
		// Fire taskID-indexed change
		propertyChangeSupport.fireIndexedPropertyChange("activeTimeInMs", taskID, before, t.getActiveTimeInMs());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt instanceof IndexedPropertyChangeEvent) {
			IndexedPropertyChangeEvent ipce = (IndexedPropertyChangeEvent) evt;
			String name = ipce.getPropertyName();
			if (null != name) {
				if (name.equals("ChangeTaskName")) {
				}
				setTaskName(ipce.getIndex(), ipce.getNewValue().toString());
			}
		}
	}

	public ArrayList<TLTask> getTaskArray() {
		return taskArray;
	}
}
