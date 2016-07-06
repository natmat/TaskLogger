package tasklogger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

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

	private TLModel() {
		taskArray = new ArrayList<>();
		pcs = new PropertyChangeSupport(this);
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

	public static void addTeamLeaderTask() {
		newTask("Team Leader");
	}
}
