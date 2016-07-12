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
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Nathan
 *
 */
public class TLModel {

	private static ArrayList<TLTask> taskArray;
	private static TLModel instance;
	private static PropertyChangeSupport pcs;
	private static ArrayList<TLTask> taskList;
	private final static String ROOT_FILE_NAME = "tasklogger"; 

	private enum CsvFormat {
		TASKNAME, TIME_IN_MS, CSV_TIME_IS_HMS
	}

	private TLModel() {
		taskArray = new ArrayList<>();
		pcs = new PropertyChangeSupport(this);
	}

	public static void addModelToView() {
		TLView.setTotalTimer(TLTask.getTotalRunTimeInMs());
		for (TLTask t : taskArray) {
			TLView.addTask(t.getTaskID());
			t.addPropertyChangeListener(TLController.getInstance());
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
		// Find task in arrayTask
		for (TLTask t : taskArray) {
			if (t.getName().equals(inName)) {
				JOptionPane.showMessageDialog(new JFrame(), "Task already exists.", "New task error",
						JOptionPane.ERROR_MESSAGE);
				return (null);
			}
		}

		TLTask task = new TLTask(inName);
		PropertyChangeSupport pcs = new PropertyChangeSupport(TLModel.getInstance());
		pcs.firePropertyChange("taskAction:" + task.getTaskID(), task.getRunning().booleanValue(), 0);
		taskArray.add(task);
		return (task);
	}

	/**
	 * @param taskID
	 */
	public void tasktButtonPressed(int taskID) {
		TLTask task = getTaskWithID(taskID);
		if (task == null) {
			return;
		}

		if (task != TLTask.getActiveTask()) {
			if (TLTask.getActiveTask() != null) {
				TLTask.getActiveTask().actionTask();
			}
		}
		task.actionTask();
	}

	public static void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public static void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	public void taskViewButtonPressed() throws Exception {
		if (taskList.isEmpty()) {
			throw (new Exception("empty taskList"));
		}
		try {
			taskList.get(0).actionTask();
			// view.setTaskState(taskList.get(0).getTaskState());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getTaskName(int inTaskID) {
		return (getTaskWithID(inTaskID).getName());
	}

	public static void printTaskTimes() {
		// Print
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime()));

		System.out.println(TLUtilities.getHMSString(TLTask.getTotalRunTimeInMs()) + " << Total time");
		for (TLTask t : taskArray) {
			System.out.println(TLUtilities.getHMSString(t.getTaskTimeInMs()) + " : " + t.getName());
		}
		System.out.println();

		exportCVSFile();
	}

	public static void setTaskName(int taskID, String taskName) {
		TLTask t = getTaskWithID(taskID);
		if (t != null) {
			t.setTitle(taskName);
		}
	}

	public static void exportCVSFile() {
//		String fileName = System.getProperty("user.dir") + "\\" + ROOT_FILE_NAME + TLUtilities.getToday() + ".csv";
		String fileName = ROOT_FILE_NAME + "_" + TLUtilities.getToday() + ".csv";
		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
			long timeValue = TLTask.getTotalRunTimeInMs();
			writer.append("Task,ms,HHmmss\n");
			writer.append("Total," + timeValue + "," + TLUtilities.getHMSString(timeValue) + "\n");
			for (TLTask t : taskArray) {
				timeValue = t.getTaskTimeInMs();
				writer.append(t.getName() + "," + timeValue + "," + TLUtilities.getHMSString(timeValue) + "\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void importCSVFile() throws FileNotFoundException, IOException {
		String fileName = ROOT_FILE_NAME + "_" + TLUtilities.getToday() + ".csv";
		File f = new File(fileName);
		if (f.exists() && !f.isDirectory()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine(); // Header
			line = br.readLine(); // Total times
			String[] input = line.split(",");
			TLTask.setTotalTime(Long.parseLong(input[CsvFormat.TIME_IN_MS.ordinal()]));
			while ((line = br.readLine()) != null) {
				// Task times
				input = line.split(",");
				TLTask t = new TLTask(input[CsvFormat.TASKNAME.ordinal()], Long.parseLong(input[CsvFormat.TIME_IN_MS.ordinal()]));
				taskArray.add(t);
			}
			br.close();
		}
	}

//	private static void deleteModel() {
//		for (TLTask t : taskArray) {
//			deleteTask(t.getTaskID());
//			t = null;
//		}
//		TLTask.setActiveTask(null);
//	}

	public static void deleteTask(int taskID) {
		TLController.deleteTask(taskID);
		TLTask t = getTaskWithID(taskID);
		TLTask.setTotalTime(TLTask.getTotalRunTimeInMs() - t.getTaskTimeInMs());
		taskArray.remove(t);
		t = null;

	}

	public static String getTaskTimeWithID(int taskID) {
		final TLTask t = getTaskWithID(taskID);
		return(TLUtilities.getHMSString(t.getTaskTimeInMs()));
	}

	public static void reset() {
		TLTask.setTotalTime(0);
		for (TLTask t : taskArray) {
			t.setActiveTime(0);
//			taskArray.remove(t);
		}
	}
}
